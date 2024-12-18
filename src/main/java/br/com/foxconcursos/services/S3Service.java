package br.com.foxconcursos.services;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.dto.S3Response;
import br.com.foxconcursos.dto.StorageInput;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3Service {
    
    private final String PREFIXO_VIDEOS = "videos/";
    private final String PREFIXO_IMAGENS = "imagens/";
    private final String PREFIXO_APOSTILAS = "apostilas/";

    private final S3Client client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public S3Service(S3Client s3Client) {
        this.client = s3Client;
    }

    public S3Response upload(StorageInput input) throws IOException {

        StringBuilder sb = new StringBuilder();

        if (input.isDocument())
            sb.append(PREFIXO_APOSTILAS);

        if (input.isImage())
            sb.append(PREFIXO_IMAGENS);

        if (input.isMovie())
            sb.append(PREFIXO_VIDEOS);

        sb.append(input.getFileName());

        String key = sb.toString();

        final long partSize = 5 * 1024 * 1024;

        CreateMultipartUploadRequest.Builder createMultipartUploadRequestBuilder = 
                CreateMultipartUploadRequest.builder()
                        .bucket(this.bucketName)
                        .key(key);
        
        if (input.isPublic())
            createMultipartUploadRequestBuilder.acl(ObjectCannedACL.PUBLIC_READ);

        CreateMultipartUploadRequest createMultipartUploadRequest = createMultipartUploadRequestBuilder.build();
        
        CreateMultipartUploadResponse createMultipartUploadResponse = 
            this.client.createMultipartUpload(createMultipartUploadRequest);

        String uploadId = createMultipartUploadResponse.uploadId();

        List<CompletedPart> completedParts = new ArrayList<>();
        byte[] buffer = new byte[(int) partSize];
        int partNumber = 1;
        long uploadedBytes = 0;

        try {
            int bytesRead;
            while ((bytesRead = input.getFileInputStream().read(buffer)) != -1) {

                byte[] partData = new byte[bytesRead];
                System.arraycopy(buffer, 0, partData, 0, bytesRead);

                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(this.bucketName)
                        .key(key)
                        .partNumber(partNumber)
                        .uploadId(uploadId)
                        .contentLength((long) bytesRead)
                        .build();

                UploadPartResponse uploadPartResponse = this.client.uploadPart(
                        uploadPartRequest, RequestBody.fromBytes(partData));

                completedParts.add(CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(uploadPartResponse.eTag())
                        .build());

                partNumber++;
                uploadedBytes += bytesRead;

                System.out.println("Parte " + (partNumber - 1) + " enviada. Total enviado: " + uploadedBytes + " bytes.");
            }

            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(this.bucketName)
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(
                        CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build();

            this.client.completeMultipartUpload(completeRequest);
            System.out.println("Upload concluído com sucesso!");
            
            String url = "https://" + bucketName + ".s3.amazonaws.com/" + key;

            return S3Response.builder()
                    .key(key)
                    .url(url)
                    .mimeType(input.getMimeType())
                    .build();

        } catch (Exception e) {
            this.client.abortMultipartUpload(
                    AbortMultipartUploadRequest.builder()
                            .bucket(this.bucketName)
                            .key(key)
                            .uploadId(uploadId)
                            .build());

            throw new RuntimeException("Erro durante o upload multipart", e);
        }

    }

    public S3Response getFile(String key) {
        
        try (S3Presigner presigner = S3Presigner.create()) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(this.bucketName)
                    .key(key)
                    .build();
            
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .getObjectRequest(getObjectRequest)
                    .build();

            URL url = presigner.presignGetObject(presignRequest).url();

            return S3Response.builder()
                .url(url.toString())
                .build();
        }
    }

}
