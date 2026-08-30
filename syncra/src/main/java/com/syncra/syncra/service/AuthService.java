package com.syncra.syncra.service;

import com.syncra.syncra.dto.AuthRequest;
import com.syncra.syncra.dto.AuthResponse;
import com.syncra.syncra.model.User;
import com.syncra.syncra.repository.UserRepository;
import com.syncra.syncra.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUserEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        User user = new User();
        user.setUserEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName());

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getUserEmail());

        return new AuthResponse(token, saved.getUserId(),
                saved.getDisplayName(), saved.getUserEmail());
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUserEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getUserEmail());

        return new AuthResponse(token, user.getUserId(),
                user.getDisplayName(), user.getUserEmail());
    }
}