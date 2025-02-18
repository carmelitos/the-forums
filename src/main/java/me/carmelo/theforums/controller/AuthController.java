package me.carmelo.theforums.controller;

import lombok.AllArgsConstructor;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {

        // Create the user
        OperationResult<Long> result = userService.registerUser(userDTO);

        return new ResponseEntity<>(
                result,
                (result.getStatus() == OperationStatus.SUCCESS) ? HttpStatus.OK : HttpStatus.BAD_REQUEST
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isEmpty())
            return ResponseEntity.badRequest().body("Invalid verification token.");

        User user = userOptional.get();
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return ResponseEntity.ok("Email verified successfully. You can now log in.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));
    }
}