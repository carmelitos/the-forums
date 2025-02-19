package me.carmelo.theforums.controller;

import lombok.RequiredArgsConstructor;
import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.model.dto.AuthenticationRequest;
import me.carmelo.theforums.model.dto.AuthenticationResponse;
import me.carmelo.theforums.model.dto.UserDTO;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<OperationResult<Long>> register(@RequestBody UserDTO dto) {
        return handleResult(userService.registerUser(dto));
    }

    @GetMapping("/verify-email/{token}")
    public ResponseEntity<OperationResult<String>> verifyEmail(@PathVariable String token) {
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

        if(!userService.hasVerifiedEmail(request.username())) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

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

    private ResponseEntity<OperationResult<Long>> handleResult(OperationResult<Long> result) {
        return ResponseEntity.status(result.getStatus() == OperationStatus.SUCCESS
                        ? HttpStatus.OK
                        : HttpStatus.BAD_REQUEST)
                .body(result);
    }
}