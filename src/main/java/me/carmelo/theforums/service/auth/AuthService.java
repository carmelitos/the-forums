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

    private static final String EMAIL_VERIFICATION_PREFIX = "email_verification:";

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
        if (userOptional.isEmpty())
            return new OperationResult<>(OperationStatus.NOT_FOUND, "Incorrect email address", "Incorrect email address");

        String redisKeyCooldown = "verification_cooldown:" + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKeyCooldown)))
            return new OperationResult<>(OperationStatus.FAILURE, "Email sent recently", "Please wait 3 minutes before requesting another email");

        String token = jwtUtil.generateEmailVerificationToken(email);
        redisTemplate.opsForValue().set(EMAIL_VERIFICATION_PREFIX + token, email, 3, TimeUnit.HOURS);

        try {
            String verificationLink = "http://localhost:4200/email-verify/" + token;
            String emailContent = verificationEmail.replace("{verification-request-to-verify}", verificationLink);
            emailService.sendHtmlEmail("noreply", email, "Verify your email address", emailContent);
        } catch (Exception e) {
            return new OperationResult<>(OperationStatus.FAILURE, "Email failed to send", "Email failed to send");
        }
        redisTemplate.opsForValue().set(redisKeyCooldown, "cooldown_active", 180, TimeUnit.SECONDS);

        return new OperationResult<>(OperationStatus.SUCCESS, "Verification email sent", "Verification email sent");
    }

    @Override
    public OperationResult<String> verifyEmail(String token) {
        if (!jwtUtil.validateToken(token))
            return new OperationResult<>(OperationStatus.FAILURE, "Invalid verification token.", null);

        if (jwtUtil.isTokenExpired(token))
            return new OperationResult<>(OperationStatus.FAILURE, "Verification token expired. Please request a new verification email.", null);

        String email = redisTemplate.opsForValue().get(EMAIL_VERIFICATION_PREFIX + token);
        if (email == null)
            return new OperationResult<>(OperationStatus.FAILURE, "Verification token not found or expired in Redis.", null);

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty())
            return new OperationResult<>(OperationStatus.FAILURE, "User not found.", null);

        User user = userOptional.get();
        if (user.isEmailVerified())
            return new OperationResult<>(OperationStatus.FAILURE, "User is already verified.", null);

        user.setEmailVerified(true);
        userRepository.save(user);

        redisTemplate.delete(EMAIL_VERIFICATION_PREFIX + token);

        return new OperationResult<>(OperationStatus.SUCCESS, "Email verified successfully.", null);
    }

    private String loadHtmlVerificationEmail() throws IOException {
        ClassPathResource resource = new ClassPathResource("email-verification-email-template.html");
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }
}