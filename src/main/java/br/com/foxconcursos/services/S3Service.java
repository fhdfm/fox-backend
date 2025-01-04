package br.com.foxconcursos.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.dto.S3Response;
import br.com.foxconcursos.dto.StorageInput;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;    

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    private S3Client getClient() {
        // Configuração do cliente HTTP (bloqueante)
        SdkHttpClient httpClient = ApacheHttpClient.builder()
            .maxConnections(50) // Ajuste conforme necessário
            .connectionTimeout(Duration.ofSeconds(60)) // Tempo limite de conexão
            .socketTimeout(Duration.ofSeconds(60)) // Tempo limite de leitura/escrita no socket
            .build();

        // Configuração personalizada para o cliente S3
        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
            .apiCallTimeout(Duration.ofMinutes(2)) // Tempo total para chamadas
            .apiCallAttemptTimeout(Duration.ofSeconds(90)) // Tempo limite por tentativa
            .build();

        // Provedor de credenciais
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        // Construção do cliente S3 bloqueante
        return S3Client.builder()
            .httpClient(httpClient)
            .overrideConfiguration(overrideConfig)
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build();
    }

    public S3Response upload(StorageInput object) {
        
        String fileName = object.getFileName();

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
                .build();
    }

    public S3Response uploadLargeFiles(StorageInput input) {

        CreateMultipartUploadResponse createMultipartUploadResponse = this.getClient().createMultipartUpload(b -> b
                .bucket(bucketName)
                .key(input.getFileName())
                .contentType(input.getMimeType()));

        String uploadId = createMultipartUploadResponse.uploadId();

        int partNumber = 1;
        int partSize = 25 * 1024 * 1024; // 10 MB
        List<Future<CompletedPart>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(5); // Número de threads

        try (BufferedInputStream bis = new BufferedInputStream(input.getFileInputStream())) {
            byte[] buffer = new byte[partSize];
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                final int partNum = partNumber++;
                final byte[] partData = new byte[bytesRead];
                System.arraycopy(buffer, 0, partData, 0, bytesRead);

                // Envia a parte em uma thread separada
                futures.add(executor.submit(() -> {
                    UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                            .bucket(bucketName)
                            .key(input.getFileName())
                            .uploadId(uploadId)
                            .partNumber(partNum)
                            .build();

                    UploadPartResponse uploadPartResponse = getClient().uploadPart(uploadPartRequest,
                            RequestBody.fromBytes(partData));

                    return CompletedPart.builder()
                            .partNumber(partNum)
                            .eTag(uploadPartResponse.eTag())
                            .build();
                }));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed during upload part process: " + e.getMessage(), e);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Executor interrupted during upload", e);
        }

        List<CompletedPart> completedParts = new ArrayList<>();
        for (Future<CompletedPart> future : futures) {
            try {
                completedParts.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to upload a part: " + e.getMessage(), e);
            }
        }

        try {
            this.getClient().completeMultipartUpload(b -> b
                    .bucket(this.bucketName)
                    .key(input.getFileName())
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to complete multipart upload: " + e.getMessage(), e);
        }

        String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, input.getFileName());
        String mimeType = input.getMimeType();

        return S3Response.builder()
                .key(input.getFileName())
                .url(fileUrl)
                .mimeType(mimeType)
                .build();
    }

    public String getFile(String key) {

        try (S3Presigner presigner = S3Presigner
                .builder()
                .region(Region.of(region))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();


            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            URL url = presigner.presignGetObject(presignRequest).url();

            return url.toString();
        }
    }

    private String getPublicUrl(String bucketName, String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
    }

}
