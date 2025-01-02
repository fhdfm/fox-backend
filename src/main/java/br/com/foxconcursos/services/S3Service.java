package br.com.foxconcursos.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
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
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .httpClient(ApacheHttpClient.builder()
                        .maxConnections(30)
                        .connectionTimeout(Duration.ofSeconds(10))
                        .connectionTimeout(Duration.ofSeconds(10))
                        .socketTimeout(Duration.ofSeconds(30))
                        .build())   
                .build();
    }

    public S3Response upload(StorageInput object) {
        
        String fileName = object.getFileName();

        try (InputStream inputStream = object.getFileInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
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

    public S3Response uploadLargeFiles(StorageInput object) throws Exception {

        final int partSize = 25 * 1024 * 1024; // 25MB

        int totalParts = (int) Math.ceil((double) object.getFileSize() / partSize);

        String fileName = object.getFileName();

        S3Client s3Client = getClient();
        CreateMultipartUploadResponse response =  s3Client.createMultipartUpload(CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build());

        String uploadId = response.uploadId();

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(totalParts, 10));
        List<Future<CompletedPart>> futures = new ArrayList<>();

        try (BufferedInputStream bis = new BufferedInputStream(object.getFileInputStream())) {
            byte[] buffer = new byte[partSize];
            int partNumber = 1;
            int bytesRead;
            
            while ((bytesRead = bis.read(buffer)) != -1) {
                final int partNum = partNumber++;
                final int readBytes = bytesRead;

                byte[] partData = new byte[readBytes];
                System.arraycopy(buffer, 0, partData, 0, readBytes);

                futures.add(executor.submit(() -> {
                    UploadPartRequest partRequest = UploadPartRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .uploadId(uploadId)
                            .partNumber(partNum)
                            .build();

                    UploadPartResponse partResponse = s3Client.uploadPart(partRequest,
                            RequestBody.fromBytes(partData));

                    return CompletedPart.builder()
                            .partNumber(partNum)
                            .eTag(partResponse.eTag())
                            .build();
                }));    
            } 
        } catch (IOException e) {
            s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .build());
            throw new RuntimeException("Erro ao dividir e enviar partes do arquivo: " + e.getMessage(), e);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        List<CompletedPart> completedParts = new ArrayList<>();
        for (Future<CompletedPart> future : futures) {
            completedParts.add(future.get());
        }

        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder()
                        .parts(completedParts)
                        .build())
                .build();

        s3Client.completeMultipartUpload(completeRequest);

        return S3Response.builder()
                .key(fileName)
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
