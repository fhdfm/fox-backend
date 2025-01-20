package br.com.foxconcursos.domain;

public enum TipoQuestao {
    
    CONCURSO(1), OAB(2), ENEM(3), MILITAR(4), VESTIBULAR (5);

    private Integer codigo;

    TipoQuestao(Integer codigo) {
        if (codigo == null || codigo < 1 || codigo > 5)
            throw new IllegalArgumentException("Código inválido: " + codigo);

        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return this.codigo;
    }

    public boolean isConcurso() {
        return this.codigo == 1;
    }

    public boolean isOAB() {
        return this.codigo == 2;
    }
    public boolean isVestibular() {return this.codigo == 5;}

    public boolean isEnem() {
        return this.codigo == 3;
    }

    public boolean isMilitar() {
        return this.codigo == 4;
    }
}
