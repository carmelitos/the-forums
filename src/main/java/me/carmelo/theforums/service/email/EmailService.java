package me.carmelo.theforums.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    @Value("${domain.email.sender}")
    private String emailDomain;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public OperationResult<Void> sendSimpleEmail(String fromUsername, String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromUsername + emailDomain);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            return new OperationResult<>(OperationStatus.SUCCESS, "Email sent successfully.", null);
        } catch (Exception e) {
            return new OperationResult<>(OperationStatus.FAILURE, "Failed to send email: " + e.getMessage(), null);
        }
    }

    public OperationResult<Void> sendHtmlEmail(String fromUsername, String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromUsername + emailDomain);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            return new OperationResult<>(OperationStatus.SUCCESS, "Email sent successfully.", null);
        } catch (MessagingException e) {
            return new OperationResult<>(OperationStatus.FAILURE, "Failed to send email: " + e.getMessage(), null);
        }
    }
}
