package me.carmelo.theforums.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.carmelo.theforums.model.dto.AuthenticationRequest;
import me.carmelo.theforums.model.dto.AuthenticationResponse;
import me.carmelo.theforums.model.dto.UserDTO;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.service.auth.SessionService;
import me.carmelo.theforums.service.user.CustomUserDetailsService;
import me.carmelo.theforums.service.user.IUserService;
import me.carmelo.theforums.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final IUserService userService;
    private final SessionService sessionService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<OperationResult<String>> register(@RequestBody UserDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            OperationResult<String> result = new OperationResult<>();
            result.setStatus(OperationStatus.FAILURE);
            result.setMessage("User is already authenticated. Logout to register a new account.");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }

        if (dto.getUsername() != null && sessionService.isSessionActive(dto.getUsername())) {
            OperationResult<String> result = new OperationResult<>();
            result.setStatus(OperationStatus.FAILURE);
            result.setMessage("Active session exists for this username. Please logout first.");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }

        OperationResult<String> responseEntity = userService.validateAndSaveUser(dto, false);
        if (responseEntity.getStatus() == OperationStatus.FAILURE) {
            return new ResponseEntity<>(responseEntity, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(responseEntity, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        if (sessionService.isSessionActive(request.username()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!userService.hasVerifiedEmail(request.username()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                ));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        sessionService.createSession(userDetails.getUsername());
        return ResponseEntity.ok(new AuthenticationResponse(
                jwtUtil.generateAccessToken(userDetails),
                jwtUtil.generateRefreshToken(userDetails)
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<OperationResult<String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Optional<String> usernameOptional = jwtUtil.extractUsernameIgnoreExpiration(token);
            usernameOptional.ifPresent(sessionService::invalidateSession);
        }
        SecurityContextHolder.clearContext();

        OperationResult<String> result = new OperationResult<>();
        result.setStatus(OperationStatus.SUCCESS);
        result.setMessage("Logout successful.");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
