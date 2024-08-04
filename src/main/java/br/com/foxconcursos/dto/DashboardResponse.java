

package br.com.foxconcursos.dto;

public class DashboardResponse {

    private int alunos;
    private int questoes;
    private int cursos;
    private int simulados;

    public DashboardResponse() {}

    public int getAlunos() {
        return alunos;
    }

    public void setAlunos(int alunos) {
        this.alunos = alunos;
    }

    public int getQuestoes() {
        return questoes;
    }

    public void setQuestoes(int questoes) {
        this.questoes = questoes;
    }

    public int getCursos() {
        return cursos;
    }

    public void setCursos(int cursos) {
        this.cursos = cursos;
    }

    public int getSimulados() {
        return simulados;
    }

    public void setSimulados(int simulados) {
        this.simulados = simulados;
    }
}

