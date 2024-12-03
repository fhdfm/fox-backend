package br.com.foxconcursos.dto;

import org.springframework.core.io.InputStreamResource;

public class GoogleDriveResponse {
    
    private InputStreamResource fileStream;
    private String mimeType;
    private String fileId;
    private String fileName;

    public GoogleDriveResponse(InputStreamResource fileStream, String mimeType, String fileId, String fileName) {
        this.fileStream = fileStream;
        this.mimeType = mimeType;
        this.fileId = fileId;
        this.fileName = fileName;
    }

    public GoogleDriveResponse(InputStreamResource fileStream, String mimeType, String fileId) {
        this.fileStream = fileStream;
        this.mimeType = mimeType;
        this.fileId = fileId;
    }

    public GoogleDriveResponse(InputStreamResource fileStream, String mimeType) {
        this.fileStream = fileStream;
        this.mimeType = mimeType;
    }

    public GoogleDriveResponse(String fileId) {
        this.fileId = fileId;
    }

    public StorageOutput get() {
        return new StorageOutput(fileStream, mimeType, fileId, fileName);
    }

}
