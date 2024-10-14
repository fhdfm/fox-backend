package br.com.foxconcursos.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("youtube_tokens")
public class YouTubeToken {
    
    @Id
    private String id;
    private String refreshToken;

    public YouTubeToken(String id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }

    public void setId(String id) { this.id = id; }
    public String getId() { return this.id; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getRefreshToken() { return this.refreshToken; }

}
