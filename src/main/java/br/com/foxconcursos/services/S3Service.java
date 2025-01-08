package br.com.foxconcursos.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.dto.S3Response;
import br.com.foxconcursos.dto.StorageInput;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
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
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
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
            .maxConnections(100) // Ajuste conforme necessário
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
        String prefix = object.getPrefix();
        fileName = prefix + "/" + fileName;

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

    public S3Response uploadLargeFiles(StorageInput input) throws IOException {
        long startTime = System.currentTimeMillis();

        String fileName = input.getFileName();
        String prefix = input.getPrefix();
        String fileNameWithPrefix = prefix + fileName;

        CreateMultipartUploadResponse createMultipartUploadResponse = this.getClient().createMultipartUpload(b -> b
                .bucket(bucketName)
                .key(fileNameWithPrefix)
                .contentType(input.getMimeType()));

        String uploadId = createMultipartUploadResponse.uploadId();
        long contentLength = input.getFileInputStream().available(); // Tamanho do arquivo
        int partSize = (int) Math.min(20 * 1024 * 1024, Math.max(contentLength / 10000, 5 * 1024 * 1024)); // Tamanho de parte ajustado dinamicamente
        int partNumber = 1;

        ExecutorService executor = Executors.newFixedThreadPool(4); // 4 threads simultâneas
        List<CompletedPart> completedParts = Collections.synchronizedList(new ArrayList<>());

        try (InputStream inputStream = input.getFileInputStream()) {
            byte[] buffer = new byte[partSize];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                final byte[] partData = Arrays.copyOf(buffer, bytesRead);
                final int currentPartNumber = partNumber++;

                executor.submit(() -> {
                    try {
                        UploadPartResponse uploadPartResponse = this.getClient().uploadPart(
                                UploadPartRequest.builder()
                                        .bucket(bucketName)
                                        .key(fileNameWithPrefix)
                                        .uploadId(uploadId)
                                        .partNumber(currentPartNumber)
                                        .build(),
                                RequestBody.fromBytes(partData)
                        );

                        completedParts.add(CompletedPart.builder()
                                .partNumber(currentPartNumber)
                                .eTag(uploadPartResponse.eTag())
                                .build());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload part " + currentPartNumber, e);
                    }
                });
            }

            executor.shutdown();
            if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                executor.shutdownNow();
                throw new RuntimeException("Timeout during multipart upload.");
            }

            completedParts.sort(Comparator.comparingInt(CompletedPart::partNumber));

            this.getClient().completeMultipartUpload(b -> b
                    .bucket(bucketName)
                    .key(fileNameWithPrefix)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build()));

        } catch (Exception e) {
            this.getClient().abortMultipartUpload(b -> b
                    .bucket(bucketName)
                    .key(fileNameWithPrefix)
                    .uploadId(uploadId));
            throw new RuntimeException("Failed during multipart upload", e);
        }

        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;

        System.out.printf("Upload completed in %d minutes and %d seconds%n",
                TimeUnit.MILLISECONDS.toMinutes(durationMillis),
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60);

        String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileNameWithPrefix);
        return S3Response.builder()
                .key(fileNameWithPrefix)
                .url(fileUrl)
                .mimeType(input.getMimeType())
                .build();
    }


    // public S3Response uploadLargeFiles(StorageInput input) {
    //     long startTime = System.currentTimeMillis(); // Registrar o horário de início

    //     String fileName = input.getFileName();
    //     String prefix = input.getPrefix();
    //     String fileNameWithPrefix = prefix + fileName;

    //     CreateMultipartUploadResponse createMultipartUploadResponse = this.getClient().createMultipartUpload(b -> b
    //             .bucket(bucketName)
    //             .key(fileNameWithPrefix)
    //             .contentType(input.getMimeType()));

    //     String uploadId = createMultipartUploadResponse.uploadId();
    //     int partSize = 20 * 1024 * 1024; // 50 MB (tamanho moderado para evitar problemas de memória)
    //     int partNumber = 1;

    //     // Executor com número controlado de threads
    //     ExecutorService executor = Executors.newFixedThreadPool(5); // 5 threads simultâneas
    //     BlockingQueue<Future<CompletedPart>> futureQueue = new ArrayBlockingQueue<>(5); // Limitar uploads simultâneos
    //     List<CompletedPart> completedParts = new ArrayList<>();

    //     try (BufferedInputStream bis = new BufferedInputStream(input.getFileInputStream())) {
    //         byte[] buffer = new byte[partSize];
    //         int bytesRead;

    //         // Ler partes do arquivo
    //         while ((bytesRead = bis.read(buffer)) != -1) {
    //             final byte[] partData = new byte[bytesRead];
    //             System.arraycopy(buffer, 0, partData, 0, bytesRead);
    //             final int currentPartNumber = partNumber++;

    //             // Enviar parte em paralelo
    //             Future<CompletedPart> future = executor.submit(() -> {
    //                 UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
    //                         .bucket(bucketName)
    //                         .key(fileNameWithPrefix)
    //                         .uploadId(uploadId)
    //                         .partNumber(currentPartNumber)
    //                         .build();

    //                 UploadPartResponse uploadPartResponse = getClient().uploadPart(uploadPartRequest,
    //                         RequestBody.fromBytes(partData));

    //                 return CompletedPart.builder()
    //                         .partNumber(currentPartNumber)
    //                         .eTag(uploadPartResponse.eTag())
    //                         .build();
    //             });

    //             futureQueue.put(future); // Bloquear se a fila de uploads estiver cheia

    //             // Processar partes concluídas assim que possível
    //             if (futureQueue.remainingCapacity() == 0) {
    //                 processCompletedParts(futureQueue, completedParts);
    //             }
    //         }

    //         // Processar partes restantes
    //         while (!futureQueue.isEmpty()) {
    //             processCompletedParts(futureQueue, completedParts);
    //         }
    //     } catch (Exception e) {
    //         throw new RuntimeException("Failed during multipart upload", e);
    //     } finally {
    //         executor.shutdown();
    //         try {
    //             if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
    //                 executor.shutdownNow();
    //             }
    //         } catch (InterruptedException e) {
    //             executor.shutdownNow();
    //             Thread.currentThread().interrupt();
    //         }
    //     }

    //     // Completar o upload
    //     try {
    //         this.getClient().completeMultipartUpload(b -> b
    //                 .bucket(this.bucketName)
    //                 .key(fileNameWithPrefix)
    //                 .uploadId(uploadId)
    //                 .multipartUpload(CompletedMultipartUpload.builder()
    //                         .parts(completedParts)
    //                         .build()));
    //     } catch (Exception e) {
    //         throw new RuntimeException("Failed to complete multipart upload", e);
    //     }

    //     long endTime = System.currentTimeMillis(); // Registrar o horário de término
    //     long durationMillis = endTime - startTime;

    //     // Converter o tempo para minutos e segundos
    //     long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
    //     long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;

    //     System.out.println("Tempo total de execução: " + minutes + " minutos e " + seconds + " segundos");

    //     String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileNameWithPrefix);
    //     return S3Response.builder()
    //             .key(fileNameWithPrefix)
    //             .url(fileUrl)
    //             .mimeType(input.getMimeType())
    //             .build();
    // }

    // // Processar partes concluídas e liberar memória
    // private void processCompletedParts(BlockingQueue<Future<CompletedPart>> futureQueue, List<CompletedPart> completedParts) {
    //     try {
    //         Future<CompletedPart> future = futureQueue.poll();
    //         if (future != null) {
    //             completedParts.add(future.get()); // Obtenha o resultado e armazene na lista
    //         }
    //     } catch (InterruptedException | ExecutionException e) {
    //         throw new RuntimeException("Failed to process completed part", e);
    //     }
    // }

    // public S3Response uploadLargeFiles(StorageInput input) {
    //     long startTime = System.currentTimeMillis(); // Registrar o horário de início
    //     CreateMultipartUploadResponse createMultipartUploadResponse = this.getClient().createMultipartUpload(b -> b
    //             .bucket(bucketName)
    //             .key(input.getFileName())
    //             .contentType(input.getMimeType()));

    //     String uploadId = createMultipartUploadResponse.uploadId();
    //     int partSize = 50 * 1024 * 1024; // 50 MB por parte
    //     int partNumber = 1;

    //     ExecutorService executor = Executors.newFixedThreadPool(5); // 5 threads simultâneas
    //     BlockingQueue<Future<CompletedPart>> futureQueue = new ArrayBlockingQueue<>(5); // Limita a fila a 5 uploads simultâneos
    //     List<CompletedPart> completedParts = new ArrayList<>();

    //     try (InputStream inputStream = input.getFileInputStream()) {
    //         byte[] buffer = new byte[partSize];
    //         int bytesRead;

    //         while ((bytesRead = inputStream.read(buffer)) != -1) {
    //             final byte[] partData = new byte[bytesRead];
    //             System.arraycopy(buffer, 0, partData, 0, bytesRead);
    //             final int currentPartNumber = partNumber++;

    //             // Submete a tarefa de upload da parte para o executor
    //             Future<CompletedPart> future = executor.submit(() -> {
    //                 UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
    //                         .bucket(bucketName)
    //                         .key(input.getFileName())
    //                         .uploadId(uploadId)
    //                         .partNumber(currentPartNumber)
    //                         .build();

    //                 UploadPartResponse uploadPartResponse = getClient().uploadPart(uploadPartRequest,
    //                         RequestBody.fromInputStream(new ByteArrayInputStream(partData), partData.length));

    //                 return CompletedPart.builder()
    //                         .partNumber(currentPartNumber)
    //                         .eTag(uploadPartResponse.eTag())
    //                         .build();
    //             });

    //             futureQueue.put(future); // Bloqueia se a fila estiver cheia

    //             // Processa partes concluídas para liberar espaço na fila
    //             if (futureQueue.remainingCapacity() == 0) {
    //                 processCompletedParts(futureQueue, completedParts);
    //             }
    //         }

    //         // Processa quaisquer partes restantes na fila
    //         while (!futureQueue.isEmpty()) {
    //             processCompletedParts(futureQueue, completedParts);
    //         }
    //     } catch (Exception e) {
    //         throw new RuntimeException("Failed during streaming upload", e);
    //     } finally {
    //         executor.shutdown();
    //         try {
    //             if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
    //                 executor.shutdownNow();
    //             }
    //         } catch (InterruptedException e) {
    //             executor.shutdownNow();
    //             Thread.currentThread().interrupt();
    //         }
    //     }

    //     // Completa o upload multipart
    //     try {
    //         this.getClient().completeMultipartUpload(b -> b
    //                 .bucket(this.bucketName)
    //                 .key(input.getFileName())
    //                 .uploadId(uploadId)
    //                 .multipartUpload(CompletedMultipartUpload.builder()
    //                         .parts(completedParts)
    //                         .build()));
    //     } catch (Exception e) {
    //         throw new RuntimeException("Failed to complete multipart upload", e);
    //     }

    //     long endTime = System.currentTimeMillis(); // Registrar o horário de término
    //     long durationMillis = endTime - startTime;
    
    //     // Converter o tempo para minutos e segundos
    //     long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
    //     long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
    
    //     System.out.println("Tempo total de execução: " + minutes + " minutos e " + seconds + " segundos");
    

    //     String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, input.getFileName());
    //     return S3Response.builder()
    //             .key(input.getFileName())
    //             .url(fileUrl)
    //             .mimeType(input.getMimeType())
    //             .build();
    // }

    // // Processa partes concluídas e remove da fila
    // private void processCompletedParts(BlockingQueue<Future<CompletedPart>> futureQueue, List<CompletedPart> completedParts) {
    //     try {
    //         Future<CompletedPart> future = futureQueue.poll();
    //         if (future != null) {
    //             completedParts.add(future.get()); // Adiciona a parte completada
    //         }
    //     } catch (InterruptedException | ExecutionException e) {
    //         throw new RuntimeException("Failed to process completed part", e);
    //     }
    // }

    public S3File getMedia(String key) {

        S3Client client = getClient();

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
        S3Client client = getClient();
        client.deleteObject(b -> b.bucket(bucketName).key(key));
        client.close();
    }

    public String getLink(String key) {

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
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
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
