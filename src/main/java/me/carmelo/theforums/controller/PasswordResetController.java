package me.carmelo.theforums.controller;

import lombok.RequiredArgsConstructor;
import me.carmelo.theforums.model.dto.ResetPasswordRequest;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.service.passwordreset.IPasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final IPasswordResetService passwordResetService;

    @PostMapping("/request-reset")
    public ResponseEntity<OperationResult<String>> requestReset(@RequestBody String email) {
        OperationResult<String> result = passwordResetService.requestPasswordReset(email);
        if (result.getStatus() == OperationStatus.FAILURE || result.getStatus() == OperationStatus.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reset")
    public ResponseEntity<OperationResult<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        OperationResult<String> result = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        if (result.getStatus() == OperationStatus.FAILURE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }
}
