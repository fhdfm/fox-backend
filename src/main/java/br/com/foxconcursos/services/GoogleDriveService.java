package br.com.foxconcursos.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import br.com.foxconcursos.dto.GoogleDriveResponse;
import br.com.foxconcursos.dto.StorageInput;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "fox-backend";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    @Value("${storage.folderId}")
    private String folderId;

    @Value("${storage.credentials}")
    private String credentialsPath;

    private Drive driveService;

    public GoogleDriveService() {
    }

    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials = getCredentials();
        this.driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private GoogleCredentials getCredentials() throws IOException, GeneralSecurityException {
        // Carregando o arquivo de credenciais a partir do caminho especificado
        InputStream credentialsStream = new FileInputStream(credentialsPath);
        return GoogleCredentials.fromStream(credentialsStream).createScoped(SCOPES);
    }

    public GoogleDriveResponse upload(StorageInput input) throws IOException {

        InputStream inputStream = input.getInputStream();

        try {

            File fileMetadata = new File();
            fileMetadata.setName(input.getFileName()); // Nome do arquivo no Google Drive
            fileMetadata.setParents(Collections.singletonList(this.folderId)); // Pasta no Google Drive

            InputStreamContent mediaContent = new InputStreamContent(input.getMimeType(), inputStream);

            Drive.Files.Create requestStorage = this.driveService.files().create(
                fileMetadata, mediaContent).setFields("id, webViewLink");

            MediaHttpUploader uploader = requestStorage.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(false);
            uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE);

            File uploadedFile = requestStorage.execute();
            
            if (input.isPublic()) {
                
                Permission publicPermission = new Permission()
                    .setType("anyone")
                    .setRole("reader");
                
                this.driveService.permissions().create(uploadedFile.getId(), publicPermission)
                        .setFields("id").execute();
            }

            return new GoogleDriveResponse(uploadedFile.getId());
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    public GoogleDriveResponse retrieveMedia(String fileId) throws IOException {

        InputStream inputStream = driveService.files().get(fileId).executeMediaAsInputStream();

        File metadata = driveService.files()
                                    .get(fileId)
                                    .setFields("name, mimeType")
                                    .execute();

        GoogleDriveResponse response = new GoogleDriveResponse(
                                        new InputStreamResource(inputStream), 
                                        metadata.getMimeType(), 
                                        fileId, metadata.getName());
        return response;

    }
}
