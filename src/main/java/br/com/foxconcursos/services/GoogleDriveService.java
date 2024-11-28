package br.com.foxconcursos.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
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

import br.com.foxconcursos.dto.GoogleDriveResponse;
import br.com.foxconcursos.dto.StorageInput;

@Service
public class GoogleDriveService {

    private final static String FOLDER_ID = "1SKWl74Pp3MbGZzencoeU2Fg7lDZC6dhJ";

    private static final String APPLICATION_NAME = "fox-backend";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);


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

        MultipartFile file = input.getInputStream();

        // Convertendo MultipartFile para java.io.File
        java.io.File convFile = new java.io.File(System.getProperty("java.io.tmpdir")
            + java.io.File.separator + file.getOriginalFilename());

        try {

            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            File fileMetadata = new File();
            fileMetadata.setName(file.getOriginalFilename()); // Nome do arquivo no Google Drive
            fileMetadata.setParents(Collections.singletonList(FOLDER_ID)); // Pasta no Google Drive

            FileContent mediaContent = new FileContent(file.getContentType(), convFile);

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
            if (convFile.exists())
                convFile.delete();
        }
    }

    public GoogleDriveResponse retrieveMedia(String fileId) throws IOException {

        InputStream inputStream = driveService.files().get(fileId).executeMediaAsInputStream();

        File metadata = driveService.files()
                                    .get(fileId)
                                    .setFields("name, mimeType")
                                    .execute();

        return new GoogleDriveResponse(
                new InputStreamResource(inputStream), 
                metadata.getMimeType(), 
                metadata.getName());

    }

    public File getFile(String fileId) throws IOException {
        File file = driveService.files().get(fileId)
        .setFields("id, name, mimeType")
        .execute();
        return file;
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

}
