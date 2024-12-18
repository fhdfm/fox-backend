package br.com.foxconcursos.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.dto.S3Response;
import br.com.foxconcursos.dto.StorageInput;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest.Builder;
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

        Builder builder = PutObjectRequest.builder();
        builder.bucket(this.bucketName);

        StringBuilder sb = new StringBuilder();

        if (input.isDocument())
            sb.append(PREFIXO_APOSTILAS);

        if (input.isImage())
            sb.append(PREFIXO_IMAGENS);

        if (input.isMovie())
            sb.append(PREFIXO_VIDEOS);

        sb.append(input.getFileName());

        String fileName = sb.toString();
        builder.key(fileName);

        if (input.isPublic()) {
            builder.acl("public-reader");
        }

        PutObjectRequest request = builder.build();

        try (InputStream fileInputStream = input.getFileInputStream()) {
           this.client.putObject(request, RequestBody.fromInputStream(fileInputStream, input.getFileSize()));
        }

        String url = "https://" + this.bucketName + ".s3." + this.region + ".amazonaws.com/" + fileName;

        return S3Response.builder()
            .key(fileName)
            .url(url)
            .mimeType(input.getMimeType())
            .build();
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
