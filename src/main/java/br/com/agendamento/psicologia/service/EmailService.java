package br.com.agendamento.psicologia.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remetente;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String remetente
    ) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    public void enviar(
            String destinatario,
            String assunto,
            String mensagem
    ) {
        validarConfiguracao();

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(remetente);
        email.setTo(destinatario);
        email.setSubject(assunto);
        email.setText(mensagem);

        mailSender.send(email);
    }

    private void validarConfiguracao() {
        if (remetente == null || remetente.isBlank()) {
            throw new IllegalStateException(
                    "O remetente de e-mail não foi configurado."
            );
        }
    }
}