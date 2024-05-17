package br.com.foxconcursos.domain;

public enum PerfilUsuario {

    ADMIN("ADMIN"),
    ALUNO("ALUNO"),
    EXTERNO("EXTERNO");

    private final String perfil;

    PerfilUsuario(String perfil) {
        this.perfil = perfil;
    }

    public String getPerfil() {
        return perfil;
    }

}
