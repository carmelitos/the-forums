package me.carmelo.theforums.service.passwordreset;

import me.carmelo.theforums.model.result.OperationResult;

public interface IPasswordResetService {

    OperationResult<String> requestPasswordReset(String email);
    OperationResult<String> resetPassword(String token, String newPassword);
}
