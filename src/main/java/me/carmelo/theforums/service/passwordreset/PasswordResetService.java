package me.carmelo.theforums.service.password;

import lombok.RequiredArgsConstructor;
import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.repository.UserRepository;
import me.carmelo.theforums.service.email.IEmailService;
import me.carmelo.theforums.service.passwordreset.IPasswordResetService;
import me.carmelo.theforums.utils.JwtUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements IPasswordResetService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final String passwordResetEmailTemplate = loadPasswordResetEmailTemplate();
    private static final String PASSWORD_RESET_PREFIX = "password_reset:";

    @Override
    public OperationResult<String> requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return new OperationResult<>(OperationStatus.NOT_FOUND,
                    "No user found with that email",
                    "No user found with that email");
        }

        String token = jwtUtil.generatePasswordResetToken(email);

        redisTemplate.opsForValue().set(
                PASSWORD_RESET_PREFIX + token,
                email,
                Duration.ofHours(1)
        );

        String resetLink = "http://localhost:4200/forgot-password-reset/" + token;
        String subject = "Password Reset Request";
        String emailContent = passwordResetEmailTemplate.replace("{password-reset-link}", resetLink);

        try {
            emailService.sendHtmlEmail("noreply", email, subject, emailContent);
        } catch (Exception e) {
            return new OperationResult<>(OperationStatus.FAILURE, "Failed to send password reset email", null);
        }

        return new OperationResult<>(OperationStatus.SUCCESS, "Password reset email sent", null);
    }

    @Override
    public OperationResult<String> resetPassword(String token, String newPassword) {
        if (!jwtUtil.validateToken(token))
            return new OperationResult<>(OperationStatus.FAILURE, "Invalid or tampered token", null);

        if (jwtUtil.isTokenExpired(token))
            return new OperationResult<>(OperationStatus.FAILURE, "Token expired, please request a new reset", null);

        String email = redisTemplate.opsForValue().get(PASSWORD_RESET_PREFIX + token);
        if (email == null)
            return new OperationResult<>(OperationStatus.FAILURE, "Token not found or expired in Redis", null);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty())
            return new OperationResult<>(OperationStatus.FAILURE, "User not found", null);

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redisTemplate.delete(PASSWORD_RESET_PREFIX + token);

        return new OperationResult<>(OperationStatus.SUCCESS, "Password updated successfully", null);
    }

    private String loadPasswordResetEmailTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("password-reset-email-template.html");
            try (InputStream inputStream = resource.getInputStream()) {
                return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load password reset email template", e);
        }
    }
}
