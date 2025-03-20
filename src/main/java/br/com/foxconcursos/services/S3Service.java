package br.com.foxconcursos.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.dto.S3Response;
import br.com.foxconcursos.dto.StorageInput;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3Service {

    @Value("${cloudflare.r2.access-key}")
    private String accessKey;
    @Value("${cloudflare.r2.secret-key}")
    private String secretKey;
    @Value("${cloudflare.r2.endpoint}")
    private String endpoint;


    private S3Client getClient() {
        S3Client s3Client = S3Client.builder()
            .region(Region.of("us-east-1"))
            .endpointOverride(URI.create(this.endpoint))
            .credentialsProvider(() -> AwsBasicCredentials.create(this.accessKey, this.secretKey))
            .build();
        return s3Client;
    }

    public S3Response upload(StorageInput object) {
        
        String fileName = object.getFileName();
        String prefix = object.getPrefix();
        fileName = prefix + fileName;

        String bucketName = null;
        if (prefix.startsWith("apostilas/")) {
            bucketName = "privado";
        }

        if (prefix.startsWith("imagens/")) {
            bucketName = "publico";
        }

        try (InputStream inputStream = object.getFileInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(object.getMimeType())
                    .build();

            getClient().putObject(request, RequestBody.fromInputStream(inputStream, object.getFileSize()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileUrl = getPublicUrl(bucketName, fileName);

        return S3Response.builder()
                .url(fileUrl)
                .key(fileName)
                .mimeType(object.getMimeType())
                .build();
    }

    public S3File getMedia(String key) {

        S3Client client = getClient();

        String bucketName = null;
        if (key.startsWith("apostilas/")) {
            bucketName = "privado";
        }

        if (key.startsWith("imagens/")) {
            bucketName = "publico";
        }

        try (ResponseInputStream<GetObjectResponse> object = client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build())) {
            
            String contentType = object.response().contentType();
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = object.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }   

            return new S3File(outputStream.toByteArray(), contentType);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer o download do arquivo do s3", e);
        } finally {
            client.close();
        }
    }

    public void delete(String key) {

        String bucketName = "";
        if (key.startsWith("apostilas/")) {
            bucketName = "privado";
        }

        if (key.startsWith("imagens/")) {
            bucketName = "publico";
        }

        S3Client client = getClient();
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        client.deleteObject(deleteRequest);
        client.close();
    }

    public String getLink(String key) {

        String bucketName = null;
        if (key.startsWith("apostilas/")) {
            bucketName = "privado";
        }

        if (key.startsWith("imagens/")) {
            bucketName = "publico";
        }

        try (S3Presigner presigner = S3Presigner
                .builder()
                .endpointOverride(URI.create(this.endpoint))
                .region(Region.of("us-east-1"))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            int expiresaAtXMinutes = 10;
            
            if (key.startsWith("video/"))
                    expiresaAtXMinutes = 120;

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expiresaAtXMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            URL url = presigner.presignGetObject(presignRequest).url();

            return url.toString();
        }
    }

    private String getPublicUrl(String bucketName, String key) {
        return String.format("https://cdn.foxconcursos.com.br/%s", key);
    }

    public static class S3File {
        private final byte[] content;
        private final String contentType;

        public S3File(byte[] content, String contentType) {
            this.content = content;
            this.contentType = contentType;
        }

        public byte[] getContent() {
            return content;
        }

        public String getContentType() {
            return contentType;
        }
    }    

}
