package me.carmelo.theforums.controller;

import lombok.RequiredArgsConstructor;
import me.carmelo.theforums.model.dto.AuthenticationRequest;
import me.carmelo.theforums.model.dto.AuthenticationResponse;
import me.carmelo.theforums.model.dto.UserDTO;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.repository.UserRepository;
import me.carmelo.theforums.service.auth.IAuthService;
import me.carmelo.theforums.service.email.IEmailService;
import me.carmelo.theforums.service.user.CustomUserDetailsService;
import me.carmelo.theforums.service.user.IUserService;
import me.carmelo.theforums.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final IUserService userService;
    private final IAuthService authService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<OperationResult<String>> register(@RequestBody UserDTO dto) {
        OperationResult<String> responseEntity = userService.validateAndSaveUser(dto, false);

        if (responseEntity.getStatus() == OperationStatus.FAILURE)
            return new ResponseEntity<>(responseEntity, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(responseEntity, HttpStatus.OK);
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<OperationResult<String>> sendVerificationEmail(@RequestBody String email) {
        OperationResult<String> result = authService.sendVerificationEmail(email);
        if (result.getStatus() == OperationStatus.FAILURE)
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<OperationResult<String>> verifyEmail(@RequestBody String token) {
        return userRepository.findByVerificationToken(token)
                .map(user -> {
                    user.setEmailVerified(true);
                    user.setVerificationToken(null);
                    userRepository.save(user);

                    OperationResult<String> result = new OperationResult<>();
                    result.setStatus(OperationStatus.SUCCESS);
                    result.setMessage("Email verified successfully");
                    return result;
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    OperationResult<String> result = new OperationResult<>();
                    result.setStatus(OperationStatus.FAILURE);
                    result.setMessage("Invalid verification token");
                    return ResponseEntity.badRequest().body(result);
                });
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {

        if (!userService.hasVerifiedEmail(request.username())) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                ));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        return ResponseEntity.ok(new AuthenticationResponse(
                jwtUtil.generateAccessToken(userDetails),
                jwtUtil.generateRefreshToken(userDetails)
        ));
    }

}