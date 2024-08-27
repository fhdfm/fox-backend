package br.com.foxconcursos.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "fox-backend";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    @Value("${storage.credentials}")
    private String credentialsPath;

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
        // Carregando o arquivo de credenciais a partir do caminho especificado
        InputStream credentialsStream = new FileInputStream(credentialsPath);
        return GoogleCredentials.fromStream(credentialsStream).createScoped(SCOPES);
    }

    @Transactional
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

    public void deleteEmptyFolder(String folderId) throws IOException {
        FileList files = driveService.files().list()
                .setQ("'" + folderId + "' in parents and trashed = false")
                .setFields("files(id, name)")
                .execute();

        if (files.getFiles().isEmpty()) {
            driveService.files().delete(folderId).execute();
        }
    }

    // Método para criar uma pasta
    public String createFolder(String folderName, String parentFolderId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if (parentFolderId != null) {
            fileMetadata.setParents(Collections.singletonList(parentFolderId));
        }

        File folder = driveService.files().create(fileMetadata)
                .setFields("id")
                .execute();

        return folder.getId();
    }

    // Método para listar arquivos de uma pasta
    public List<File> listFilesInFolder(String folderId) throws IOException {
        FileList result = driveService.files().list()
                .setQ("'" + folderId + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                .setFields("files(id, name)")
                .execute();

        return result.getFiles();
    }

    // Método para listar subpastas de uma pasta
    public List<File> listFoldersInFolder(String folderId) throws IOException {
        FileList result = driveService.files().list()
                .setQ("'" + folderId + "' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false")
                .setFields("files(id, name)")
                .execute();

        return result.getFiles();
    }

    // Método para listar tudo de uma pasta (arquivos e subpastas)
    public List<File> listAllInFolder(String folderId) throws IOException {
        FileList result = driveService.files().list()
                .setQ("'" + folderId + "' in parents and trashed = false")
                .setFields("files(id, name, mimeType)")
                .execute();

        return result.getFiles();
    }    
}
