package br.com.foxconcursos.dto;

import java.util.UUID;

import br.com.foxconcursos.domain.ItemQuestaoSimulado;

public class ItemQuestaoResponse {
    
    private UUID id;
    private UUID questaoId;
    private Integer ordem;
    private String descricao;
    private Boolean correta;
    private boolean itemMarcado = false;

    public ItemQuestaoResponse() {
    }

    public ItemQuestaoResponse(ItemQuestaoSimulado item) {
        this.id = item.getId();
        this.ordem = item.getOrdem();
        this.descricao = item.getDescricao();
        this.questaoId = item.getQuestaoSimuladoId();
        this.correta = item.getCorreta();
    }

    public UUID getId() {
        return id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public UUID getQuestaoId() {
        return questaoId;
    }

    public Boolean getCorreta() {
        return correta;
    }

    public void setItemMarcado(Boolean itemMarcado) {
        this.itemMarcado = itemMarcado;
    }

    public Boolean getItemMarcado() {
        return this.itemMarcado;
    }
}
