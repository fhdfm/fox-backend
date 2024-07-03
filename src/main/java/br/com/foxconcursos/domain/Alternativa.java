package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.AlternativaRequest;

@Table("alternativas")
public class Alternativa {
    
    @Id
    private UUID id;
    private UUID questaoId;
    private String letra;
    private String descricao;
    private Boolean correta;
    @Version
    private int version;

    public Alternativa() {

    }

    public Alternativa(AlternativaRequest request, UUID questaoId) {
        
        this.questaoId = questaoId;
        this.letra = request.getOrdem();
        this.descricao = request.getDescricao();
        this.correta = request.getCorreta();
        
        if (request.getId() != null) {
            this.id = request.getId();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuestaoId() {
        return questaoId;
    }

    public void setQuestaoId(UUID questaoId) {
        this.questaoId = questaoId;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getCorreta() {
        return correta;
    }

    public void setCorreta(Boolean correta) {
        this.correta = correta;
    }

}
