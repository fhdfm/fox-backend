package br.com.foxconcursos.services;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import br.com.foxconcursos.repositories.QuestaoRepository;

@Component
public class QuestaoProcessor {
    
    private final GptService gptService;
    private final QuestaoRepository questaoRepository;

    public QuestaoProcessor(GptService gptService, QuestaoRepository questaoRepository) {
        this.gptService = gptService;
        this.questaoRepository = questaoRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void iniciarProcessamento() {
        System.out.println("Iniciando o processamento... ");

        questaoRepository.findIdsByComentadaFalse().forEach(questaoId -> {
            try {
                gptService.processarQuestao(questaoId);
            } catch (Exception e) {
                System.err.println("Erro ao processar a questão: " + questaoId + " - " + e.getMessage());
            }
        });

        System.out.println("Finalizando o processamento");
    }

}
