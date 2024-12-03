package br.com.foxconcursos.dto;

import org.springframework.core.io.InputStreamResource;

public class StorageOutput {
    
    private InputStreamResource fileStream;
    private String mimeType;
    private String fileId;
    private String fileName;

    private String videoUrl;
    private String thumbnailUrl;

    public StorageOutput(String videoUrl, String thumnailUrl) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumnailUrl;
    }

    public StorageOutput(InputStreamResource fileStream, String mimeType, String fileId) {
        this.fileStream = fileStream;
        this.mimeType = mimeType;
        this.fileId = fileId;
    }

    public StorageOutput(InputStreamResource fileStream, String mimeType) {
        this.fileStream = fileStream;
        this.mimeType = mimeType;
    }

    public InputStreamResource getFileStream() {
        return this.fileStream;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getFileId() {
        return this.fileId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

}
