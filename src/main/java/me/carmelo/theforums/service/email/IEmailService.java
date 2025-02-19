package me.carmelo.theforums.service.email;

import me.carmelo.theforums.model.result.OperationResult;

public interface IEmailService {

    OperationResult<Void> sendSimpleEmail(String fromUsername, String to, String subject, String text);
    OperationResult<Void> sendHtmlEmail(String fromUsername, String to, String subject, String htmlContent);
}
