package br.com.foxconcursos.services.tmp;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Alternativa;
import br.com.foxconcursos.domain.Questao;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.repositories.AlternativaRepository;
import br.com.foxconcursos.repositories.QuestaoRepository;
import br.com.foxconcursos.services.StorageService;

@Service
public class ImagemProcessorService {

    private StorageService storageService;
    private QuestaoRepository questaoRepository;
    private AlternativaRepository alternativaRepository;

    public ImagemProcessorService(StorageService storageService, QuestaoRepository questaoRepository, AlternativaRepository alternativaRepository) {
        this.storageService = storageService;
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
    }

    // @PostConstruct
    @Transactional
    public void processarQuestoes() {
        try {
            List<Questao> questoes = questaoRepository.findByProcessadoFalse();
            for (Questao questao : questoes) {
                String enunciadoComImagens = processarBase64(questao.getEnunciado());
                questao.setEnunciado(enunciadoComImagens);
                questao.setProcessado(true);
                questaoRepository.save(questao);

                processarAlternativas(questao.getId());
            }
        } catch (Exception e) {
            // Logando a exceção e mantendo o fluxo
            e.printStackTrace();
        }
    }

    private void processarAlternativas(UUID questaoId) {
        try {
            List<Alternativa> alternativas = alternativaRepository.findByQuestaoId(questaoId);
            for (Alternativa alternativa : alternativas) {
                String descricaoComImagens = processarBase64(alternativa.getDescricao());
                alternativa.setDescricao(descricaoComImagens);
                alternativaRepository.save(alternativa);
            }
        } catch (Exception e) {
            // Logando a exceção e mantendo o fluxo
            e.printStackTrace();
        }
    }

    private String processarBase64(String conteudo) {
        String regex = "data:image/(.*?);base64,([A-Za-z0-9+/=]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(conteudo);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String base64Imagem = matcher.group(2);
            String mimeType = matcher.group(1);
            String fileExtension = getFileExtensionFromMimeType(mimeType);
            String fileName = "imagem_" + UUID.randomUUID().toString() + fileExtension;

            try {
                StorageOutput storageOutput = uploadImagemBase64(base64Imagem, fileName);
                matcher.appendReplacement(sb, "src=\"" + storageOutput.getUrl() + "\"");
            } catch (Exception e) {
                // Logando erro no upload de imagem e mantendo o processo
                e.printStackTrace();
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private StorageOutput uploadImagemBase64(String base64Image, String fileName) throws Exception {
        String mimeType = "application/octet-stream";

        if (base64Image.contains("data:image")) {
            String[] parts = base64Image.split(",");
            String dataPart = parts[0];
            mimeType = dataPart.split(":")[1].split(";")[0];
            base64Image = parts[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        StorageInput input = new StorageInput.Builder()
                .withFileInputStream(new ByteArrayInputStream(imageBytes))
                .withFileName(fileName)
                .withMimeType(mimeType)
                .withFileSize(imageBytes.length)
                .isPublic(true)
                .build();

        return this.storageService.upload(input);
    }

    private String getFileExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "png": return ".png";
            case "jpeg":
            case "jpg": return ".jpg";
            case "gif": return ".gif";
            case "bmp": return ".bmp";
            case "webp": return ".webp";
            default: return ".bin";
        }
    }
}
