package br.com.foxconcursos.executors;

import java.util.UUID;

public interface TarefaExecutor {
    void executar(UUID targetId);
}
