package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Endereco;
import br.com.foxconcursos.domain.TipoProduto;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String destinatario, String assunto,
                          String textoEmHtml) throws Exception {

        MimeMessage message = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(destinatario);
        helper.setSubject(assunto);
        helper.setText(textoEmHtml, true);
        helper.setFrom("cursofoxportal@gmail.com");

        this.mailSender.send(message);
    }

    public void enviarEmailPagamentoProcessando(String emailAluno, String nomeAluno, String produto) {
        String emailAdmin = "foxnaval9@gmail.com";
        String assunto = "Seu pagamento está em processamento";

        String mensagemAluno = "<h2>Olá, " + nomeAluno + "!</h2>"
                + "<p>Seu pagamento para o produto <strong>" + produto + "</strong> está sendo processado.</p>"
                + "<p>Em breve, você receberá uma atualização sobre a aprovação do seu pagamento.</p>"
                + "<p>Atenciosamente,<br/>Equipe Curso Fox</p>";

        String mensagemAdmin = "<h2>Pagamento em Processamento</h2>"
                + "<p>Um novo pagamento está sendo processado.</p>"
                + "<p>Aluno: <strong>" + nomeAluno + "</strong></p>"
                + "<p>Produto: <strong>" + produto + "</strong></p>";

        try {
            sendEmail(emailAluno, assunto, mensagemAluno);
            sendEmail(emailAdmin, "Novo pagamento em processamento", mensagemAdmin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviarEmailPagamentoAprovado(String emailAluno, String nomeAluno, String produto, TipoProduto tipo, Endereco endereco) {
        String emailAdmin = "foxnaval9@gmail.com";
        String assunto = "Pagamento aprovado!";

        // E-mail para o aluno
        String mensagemAluno = "<h2>Parabéns, " + nomeAluno + "!</h2>"
                + "<p>Seu pagamento para o produto <strong>" + produto + "</strong> foi aprovado com sucesso!</p>"
                + "<p>Agora você pode acessar o conteúdo adquirido.</p>"
                + "<p>Atenciosamente,<br/>Equipe Curso Fox</p>";

        // E-mail para o administrador
        String mensagemAdmin = "<h2>Pagamento Aprovado</h2>"
                + "<p>Um pagamento foi aprovado.</p>"
                + "<p>Aluno: <strong>" + nomeAluno + "</strong></p>"
                + "<p>Email: <strong>" + emailAluno + "</strong></p>"
                + "<p>Produto: <strong>" + produto + "</strong></p>";

        System.out.println("ENVIO DE EMAIL");
        System.out.println(endereco);
        System.out.println(tipo);
        System.out.println("ENVIO DE EMAIL");

        if (tipo.equals(TipoProduto.APOSTILA)  && endereco != null) {
            mensagemAdmin += "<h3>📦 Dados para Entrega:</h3>"
                    + "<p><strong>Endereço:</strong> " + endereco.getLogradouro() + ", " + endereco.getNumero() + "</p>"
                    + (endereco.getComplemento() != null ? "<p><strong>Complemento:</strong> " + endereco.getComplemento() + "</p>" : "")
                    + "<p><strong>Bairro:</strong> " + endereco.getBairro() + "</p>"
                    + "<p><strong>Cidade:</strong> " + endereco.getCidade() + " - " + endereco.getEstado() + "</p>"
                    + "<p><strong>CEP:</strong> " + endereco.getCep() + "</p>";
        }

        try {
            sendEmail(emailAluno, assunto, mensagemAluno);
            sendEmail(emailAdmin, "Pagamento aprovado", mensagemAdmin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
