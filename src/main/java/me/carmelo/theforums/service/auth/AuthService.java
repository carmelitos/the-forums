package me.carmelo.theforums.service.auth;

import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.repository.UserRepository;
import me.carmelo.theforums.service.email.IEmailService;
import me.carmelo.theforums.utils.JwtUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService implements IAuthService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final IEmailService emailService;
    private final UserRepository userRepository;
    private final String verificationEmail;

    public AuthService(RedisTemplate<String, String> redisTemplate, IEmailService emailService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;

        try {
            this.verificationEmail =  loadHtmlVerificationEmail();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public OperationResult<String> sendVerificationEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return new OperationResult<>(OperationStatus.NOT_FOUND, "Incorrect email address", "Incorrect email address");
        }

        String redisKey = "verification_cooldown:" + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return new OperationResult<>(OperationStatus.FAILURE, "Email sent recently", "Please wait 3 minutes before requesting another email");
        }

        String token = userOptional.get().getVerificationToken();
        if(token == null) new OperationResult<>(OperationStatus.FAILURE, "Token not found?", "Token not found?");

        try {
            emailService.sendHtmlEmail("noreply", email, "Verify your email address", verificationEmail.replace("{verification-request-to-verify}", "http://localhost:4200/email-verify/" + token));
        } catch (Exception e) {
            return new OperationResult<>(OperationStatus.FAILURE, "Email failed to send", "Email failed to send");
        }
        redisTemplate.opsForValue().set(redisKey, "cooldown_active", 180, TimeUnit.SECONDS);

        return new OperationResult<>(OperationStatus.SUCCESS, "Verification email sent", "Verification email sent");
    }

    @Override
    public OperationResult<String> verifyEmail(String token) {
        if (!jwtUtil.validateToken(token)) {
            OperationResult<String> result = new OperationResult<>();
            result.setStatus(OperationStatus.FAILURE);
            result.setMessage("Invalid verification token.");
            return result;
        }

        if (jwtUtil.isTokenExpired(token)) {
            OperationResult<String> result = new OperationResult<>();
            result.setStatus(OperationStatus.FAILURE);
            result.setMessage("Verification token expired. Please request a new verification email.");
            return result;
        }

        Optional<String> emailOptional = jwtUtil.extractUsername(token);
        if (emailOptional.isEmpty()) {
            OperationResult<String> result = new OperationResult<>();
            result.setStatus(OperationStatus.FAILURE);
            result.setMessage("Invalid token: missing subject.");
            return result;
        }
        String email = emailOptional.get();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            OperationResult<String> result = new OperationResult<>();
            result.setStatus(OperationStatus.FAILURE);
            result.setMessage("User not found.");
            return result;
        }

        User user = userOptional.get();
        if (user.isEmailVerified()) {
            OperationResult<String> result = new OperationResult<>();
            result.setStatus(OperationStatus.FAILURE);
            result.setMessage("User is already verified.");
            return result;
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        OperationResult<String> result = new OperationResult<>();
        result.setStatus(OperationStatus.SUCCESS);
        result.setMessage("Email verified successfully.");
        return result;
    }

    private String loadHtmlVerificationEmail() throws IOException {
        ClassPathResource resource = new ClassPathResource("email-verification-email-template.html");
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }
}