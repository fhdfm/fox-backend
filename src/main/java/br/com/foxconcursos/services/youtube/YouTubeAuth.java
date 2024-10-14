package br.com.foxconcursos.services.youtube;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTubeScopes;

import br.com.foxconcursos.repositories.YouTubeTokenRepository;

@Component
public class YouTubeAuth {
    
    @Value("${youtube.credentials}")
    private String credentialsPath;

    @Value("${youtube.userId}")
    private String userId;    

    private final YouTubeTokenRepository repository;

    private static final Collection<String> SCOPES = Collections.singleton(YouTubeScopes.YOUTUBE_UPLOAD);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private GoogleClientSecrets clientSecrets;

    public YouTubeAuth(YouTubeTokenRepository repository) { this.repository = repository; }

    @PostConstruct
    public void init() throws IOException {
        loadClientSecrets();
    }

    private void loadClientSecrets() throws IOException {
        if (this.clientSecrets == null) {
            this.clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, 
                    new InputStreamReader(
                        new FileInputStream(credentialsPath)));
        }
    }

    private Credential authorize(String userId) throws IOException, GeneralSecurityException {

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), 
                JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888)  // Porta fixa
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");                
        
        String refreshToken = credential.getRefreshToken();
        if  (refreshToken != null) {
            this.repository.insertToken(userId, refreshToken);
        }

        return credential;
    }

    public Credential getAccessToken() throws IOException, GeneralSecurityException {
        
        try {

            String clientId = this.clientSecrets.getDetails().getClientId();
            String clientSecret = this.clientSecrets.getDetails().getClientSecret();

            String refreshToken = this.repository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("Refresh Token não encontrado para o usuário: " + userId))
                        .getRefreshToken();

            TokenResponse tokenResponse = new GoogleRefreshTokenRequest(
                    GoogleNetHttpTransport.newTrustedTransport(), 
                    JSON_FACTORY, refreshToken, 
                    clientId, 
                    clientSecret)
                    .execute();

            return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JSON_FACTORY)
                    .build()
                    .setFromTokenResponse(tokenResponse);
        } catch (Exception e) {
            return this.authorize(userId);
        }

    }
}
