package me.carmelo.theforums.service.auth;

import me.carmelo.theforums.model.result.OperationResult;

public interface IAuthService {
    OperationResult<String> sendVerificationEmail(String email);
}
