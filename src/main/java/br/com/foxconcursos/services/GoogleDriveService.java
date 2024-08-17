package br.com.foxconcursos.services;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "fox-backend";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/fox-credentials.json";

    private Drive driveService;

    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials = getCredentials();
        this.driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private GoogleCredentials getCredentials() throws IOException, GeneralSecurityException {
        InputStream in = new ClassPathResource(CREDENTIALS_FILE_PATH).getInputStream();
        return GoogleCredentials.fromStream(in).createScoped(SCOPES);
    }

    public String uploadFile(java.io.File file, String mimeType, String folderId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(file.getName());

        if (folderId != null) {
            fileMetadata.setParents(Collections.singletonList(folderId));
        }

        FileContent mediaContent = new FileContent(mimeType, file);
        File uploadedFile = this.driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return uploadedFile.getId();
    }

    public String getUrl(String fileId) throws IOException {
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");

        this.driveService.permissions().create(fileId, permission).execute();

        File file = this.driveService.files().get(fileId)
                .setFields("webViewLink, webContentLink")
                .execute();

        return file.getWebViewLink();
    }
}
