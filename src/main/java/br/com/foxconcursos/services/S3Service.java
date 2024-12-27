package br.com.foxconcursos.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.dto.S3Response;
import br.com.foxconcursos.dto.StorageInput;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

@Service
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    private S3Client client;

    public S3Service(S3Client client) {
        this.client = client;
    }

    public S3Response upload(StorageInput input) {

        CreateMultipartUploadResponse createMultipartUploadResponse = this.client.createMultipartUpload(b -> b
                .bucket(bucketName)
                .key(input.getFileName()));

        String uploadId = createMultipartUploadResponse.uploadId();

        int partNumber = 1;
        List<CompletedPart> completedParts = new ArrayList<>();
        ByteBuffer bb = ByteBuffer.allocate(1024 * 1024 * 10);
        
        try (BufferedInputStream bis = new BufferedInputStream(input.getFileInputStream())) {
            
            int bytesRead;
            
            while ((bytesRead = bis.read(bb.array())) != -1) {
                bb.limit(bytesRead);
                
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(input.getFileName())
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build();

                UploadPartResponse uploadPartResponse = client.uploadPart(uploadPartRequest,
                        RequestBody.fromByteBuffer(bb));

                CompletedPart completedPart = CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(uploadPartResponse.eTag())
                        .build();

                completedParts.add(completedPart);
                partNumber++;
                bb.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }

        try {

            this.client.completeMultipartUpload(b -> b
                .bucket(this.bucketName)
                .key(input.getFileName())
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder()
                    .parts(completedParts)
                    .build()));
        } catch (Exception e) {
            e.printStackTrace();;
        }

        return null;
    }

}
