package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("respostas_simulado_questao")
public class RespostaSimuladoQuestao {
    
    @Id
    private UUID id;
    private UUID respostaSimuladoId;
    private UUID questaoId;
    private UUID itemQuestaoId;
    private boolean correta;
    @Version
    private int version;

    public RespostaSimuladoQuestao() {

    }

    public RespostaSimuladoQuestao(UUID respostaSimuladoId, UUID questaoId, 
        UUID itemQuestaoId, boolean correta) {
        
        this.respostaSimuladoId = respostaSimuladoId;
        this.questaoId = questaoId;
        this.itemQuestaoId = itemQuestaoId;
        this.correta = correta;

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRespostaSimuladoId() {
        return respostaSimuladoId;
    }

    public void setRespostaSimuladoId(UUID respostaSimuladoId) {
        this.respostaSimuladoId = respostaSimuladoId;
    }

    public UUID getQuestaoId() {
        return questaoId;
    }

    public void setQuestaoId(UUID questaoId) {
        this.questaoId = questaoId;
    }

    public UUID getItemQuestaoId() {
        return itemQuestaoId;
    }

    public void setItemQuestaoId(UUID itemQuestaoId) {
        this.itemQuestaoId = itemQuestaoId;
    }

    public boolean isCorreta() {
        return correta;
    }

    public void setCorreta(boolean correta) {
        this.correta = correta;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
