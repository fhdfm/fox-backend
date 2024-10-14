package br.com.foxconcursos.repositories;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.YouTubeToken;

@Repository
public interface YouTubeTokenRepository extends CrudRepository<YouTubeToken, String> {

    @Modifying
    @Query("INSERT INTO youtube_tokens (id, refresh_token) VALUES (:id, :refreshToken)")
    void insertToken(@Param("id") String id, @Param("refreshToken") String refreshToken);

}
