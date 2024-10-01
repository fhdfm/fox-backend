package br.com.foxconcursos.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

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
    public void sendEmailSAC(String remetente, String produto,
        String textoEmHtml) throws Exception {

        MimeMessage message = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("cursofoxportal@gmail.com");
        helper.setSubject(produto);
        helper.setText(textoEmHtml, true);
        helper.setFrom(remetente);

        this.mailSender.send(message);
    }

}
