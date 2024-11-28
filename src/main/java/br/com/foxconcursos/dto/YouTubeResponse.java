package br.com.foxconcursos.dto;

public class YouTubeResponse {
    
    private String videoUrl;
    private String thumbnailUrl;

    public YouTubeResponse(String videoUrl, String thumbnailUrl) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public StorageOutput get() {
        return new StorageOutput(videoUrl, thumbnailUrl);
    }

}
