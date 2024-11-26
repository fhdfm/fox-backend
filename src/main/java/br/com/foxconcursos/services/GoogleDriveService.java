package br.com.foxconcursos.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import br.com.foxconcursos.domain.Storage;
import br.com.foxconcursos.domain.TipoArquivo;
import br.com.foxconcursos.dto.StorageRequest;
import br.com.foxconcursos.repositories.StorageRepository;

@Service
public class GoogleDriveService {

    private final static String FOLDER_ID = "1SKWl74Pp3MbGZzencoeU2Fg7lDZC6dhJ";

    private static final String APPLICATION_NAME = "fox-backend";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    private final StorageRepository repository;

    @Value("${storage.credentials}")
    private String credentialsPath;

    private Drive driveService;

    public GoogleDriveService(StorageRepository repository) {
        this.repository = repository;
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


    private void validateRequest(StorageRequest request) {

        MultipartFile file = request.getFile();
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Campo file é requerido.");

        String folderId = request.getFolderId();
        if (folderId == null || folderId.isEmpty())
            throw new IllegalArgumentException("Campo folderId é requerido.");

        TipoArquivo tipoArquivo = request.getTipo();
        if (tipoArquivo == null)
            throw new IllegalArgumentException("Campo tipo é requerido.");

        UUID disciplinaId = request.getDisciplinaId();
        if (disciplinaId == null)
            throw new IllegalArgumentException("Campo disciplinaId é requerido.");

        UUID assuntoId = request.getAssuntoId();
        if (assuntoId == null)
            throw new IllegalArgumentException("Campo assuntoId é requerido.");
    }

    public String upload(MultipartFile file) throws IOException {

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
            Permission publicPermission = new Permission()
                    .setType("anyone")
                    .setRole("reader");

            Drive.Files.Create requestStorage = this.driveService.files().create(
                fileMetadata, mediaContent).setFields("id, webViewLink");

            MediaHttpUploader uploader = requestStorage.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(false);
            uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE);

            File uploadedFile = requestStorage.execute();
            this.driveService.permissions().create(uploadedFile.getId(), publicPermission)
                    .setFields("id").execute();

            return uploadedFile.getId();
        } finally {
            if (convFile.exists())
                convFile.delete();
        }
    }

    @Transactional
    public UUID upload(StorageRequest request) throws IOException {

        this.validateRequest(request);

        // Pegando o arquivo do StorageRequest
        MultipartFile multipartFile = request.getFile();
        String folderId = request.getFolderId();

        // Convertendo MultipartFile para java.io.File
        java.io.File convFile = new java.io.File(System.getProperty("java.io.tmpdir")
            + java.io.File.separator + multipartFile.getOriginalFilename());

        try {

            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(multipartFile.getBytes());
            fos.close();

            // Criando o metadata do arquivo para Google Drive
            File fileMetadata = new File();
            fileMetadata.setName(multipartFile.getOriginalFilename()); // Nome do arquivo no Google Drive
            fileMetadata.setParents(Collections.singletonList(folderId)); // Pasta no Google Drive

            // Preparando o conteúdo do arquivo
            FileContent mediaContent = new FileContent(multipartFile.getContentType(), convFile);

            // Fazendo o upload do arquivo no Google Drive
            // File uploadedFile = this.driveService.files().create(fileMetadata, mediaContent)
            //         .setFields("id, webViewLink, thumbnailLink")
            //         .execute();

            Drive.Files.Create requestStorage = this.driveService.files().create(
                fileMetadata, mediaContent).setFields("id, webViewLink, thumbnailLink");

            MediaHttpUploader uploader = requestStorage.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(false);
            uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE);

            File uploadedFile = requestStorage.execute();

            // Retornando o link do arquivo no Google 
            String id = uploadedFile.getId();

            String fullFolderPath = getFullFolderPath(folderId);

            String thumbnailLink = uploadedFile.getThumbnailLink();

            // Salva no banco de dados...
            Storage storage = new Storage();
            storage.setUrl(id);
            storage.setAssuntoId(request.getAssuntoId());
            storage.setDisciplinaId(request.getDisciplinaId());
            storage.setTipo(TipoArquivo.APOSTILA);
            storage.setFolder(fullFolderPath);
            storage.setThumbnail(thumbnailLink);

            this.repository.save(storage);

            return storage.getId();
        } finally {
            if (convFile.exists())
                convFile.delete();
        }
    }

    private String getFullFolderPath(String folderId) throws IOException {
        StringBuilder path = new StringBuilder();
        String currentFolderId = folderId;

        while (currentFolderId != null && !currentFolderId.equals("root")) {
            // Recupera os metadados da pasta pelo ID
            File folder = this.driveService.files().get(currentFolderId)
                    .setFields("id, name, parents")
                    .execute();

            // Prepend o nome da pasta ao caminho
            path.insert(0, "/" + folder.getName());

            // Atualiza o ID para subir na hierarquia
            if (folder.getParents() != null && !folder.getParents().isEmpty()) {
                currentFolderId = folder.getParents().get(0);
            } else {
                currentFolderId = null; // Chegou ao topo (raiz)
            }
        }

        return path.toString(); // Caminho completo da pasta
    }

    public InputStreamResource retrieveMedia(String fileId) throws IOException {

        InputStream inputStream = driveService.files().get(fileId).executeMediaAsInputStream();

        return new InputStreamResource(inputStream);

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

    public List<File> list(String type, String folderId) throws IOException {

        if ("FOLDERS".equalsIgnoreCase(type))
            return this.listFoldersInFolder(folderId);

        if ("FILES".equalsIgnoreCase(type))
            return this.listFilesInFolder(folderId);

        return this.list(folderId);
    }

    public List<File> list(String folderId) throws IOException {
        return this.listAllInFolder(folderId);
    }

    // Método para listar arquivos de uma pasta
    private List<File> listFilesInFolder(String folderId) throws IOException {
        FileList result = driveService.files().list()
                .setQ("'" + folderId + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                .setFields("files(id, name)")
                .execute();

        return result.getFiles();
    }

    // Método para listar subpastas de uma pasta
    private List<File> listFoldersInFolder(String folderId) throws IOException {
        FileList result = driveService.files().list()
                .setQ("'" + folderId + "' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false")
                .setFields("files(id, name)")
                .execute();

        return result.getFiles();
    }

    // Método para listar tudo de uma pasta (arquivos e subpastas)
    private List<File> listAllInFolder(String folderId) throws IOException {
        FileList result = driveService.files().list()
                .setQ("'" + folderId + "' in parents and trashed = false")
                .setFields("files(id, name, mimeType)")
                .execute();

        return result.getFiles();
    }
}
