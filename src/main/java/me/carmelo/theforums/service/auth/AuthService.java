package me.carmelo.theforums.service.auth;

import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.repository.UserRepository;
import me.carmelo.theforums.service.email.IEmailService;
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

    private final RedisTemplate<String, String> redisTemplate;
    private final IEmailService emailService;
    private final UserRepository userRepository;
    private String verificationEmail;

    public AuthService(RedisTemplate<String, String> redisTemplate, IEmailService emailService, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
        this.userRepository = userRepository;

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

        //TODO implement a frontend page to redirect where the user can click a button to send the down described api request
        emailService.sendHtmlEmail("noreply", email, "Verify your email address", verificationEmail.replace("{verification-request-to-verify}", "http://localhost:8080/api/auth/verify-email/" + token));

        redisTemplate.opsForValue().set(redisKey, "cooldown_active", 180, TimeUnit.SECONDS);

        return new OperationResult<>(OperationStatus.SUCCESS, "Verification email sent", "Verification email sent");
    }


    private String loadHtmlVerificationEmail() throws IOException {
        ClassPathResource resource = new ClassPathResource("email-verification-email-template.html");
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }
}