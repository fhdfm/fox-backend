package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.ItemQuestaoSimuladoRequest;

@Table("itens_questao_simulado")
public class ItemQuestaoSimulado {
    
    @Id
    private UUID id;
    private UUID questaoSimuladoId;
    private Integer ordem;
    private String descricao;
    private Boolean correta;
    @Version
    private int version;

    public ItemQuestaoSimulado() {

    }

    public ItemQuestaoSimulado(ItemQuestaoSimuladoRequest request) {
        this.ordem = request.getOrdem();
        this.descricao = request.getDescricao();
        this.correta = request.getCorreta();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuestaoSimuladoId() {
        return questaoSimuladoId;
    }

    public void setQuestaoSimuladoId(UUID questaoSimuladoId) {
        this.questaoSimuladoId = questaoSimuladoId;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
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
