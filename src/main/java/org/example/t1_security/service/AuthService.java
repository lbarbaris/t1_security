package org.example.t1_security.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.t1_security.dto.AuthResponse;
import org.example.t1_security.dto.LoginRequest;
import org.example.t1_security.dto.RefreshRequest;
import org.example.t1_security.dto.RegisterRequest;
import org.example.t1_security.model.RoleEnum;
import org.example.t1_security.model.Token;
import org.example.t1_security.model.User;
import org.example.t1_security.repository.RoleRepository;
import org.example.t1_security.repository.TokenRepository;
import org.example.t1_security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        var roleEnum = request.getRole() != null ? request.getRole() : RoleEnum.GUEST;
        var role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleEnum));

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))
                .enabled(true)
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var token = Token.builder()
                .user(user)
                .token(refreshToken)
                .revoked(false)
                .expired(false)
                .createdAt(java.time.Instant.now())
                .build();

        tokenRepository.save(token);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(RefreshRequest request) {
        var refreshToken = request.getRefreshToken();

        var storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            throw new RuntimeException("Invalid refresh token");
        }

        var user = storedToken.getUser();

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Token invalid or expired");
        }

        var newAccessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(newAccessToken, refreshToken);
    }

    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }

        String token = authHeader.substring(7);

        var storedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        storedToken.setRevoked(true);
        storedToken.setExpired(true);

        tokenRepository.save(storedToken);
    }

}

