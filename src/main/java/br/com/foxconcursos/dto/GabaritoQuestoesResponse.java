package br.com.foxconcursos.dto;

import br.com.foxconcursos.util.FoxUtils;

public class GabaritoQuestoesResponse {
    
    private int ordem;
    private int resposta;

    public GabaritoQuestoesResponse(int ordem, int resposta) {
        this.ordem = ordem;
        this.resposta = resposta;
    }

    // Getters and setters

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public char getResposta() {
        return FoxUtils.obterLetra(resposta);
    }

    public void setResposta(int resposta) {
        this.resposta = resposta;
    }
}
