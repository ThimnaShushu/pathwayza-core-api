package com.pathwayza.core_api.controller;

import com.pathwayza.core_api.model.Learner;
import com.pathwayza.core_api.repository.LearnerRepository;
import com.pathwayza.core_api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LearnerRepository learnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Learner learner) {
        learner.setPasswordHash(passwordEncoder.encode(learner.getPasswordHash()));
        learnerRepository.save(learner);
        String token = jwtUtil.generateToken(learner.getEmail());
        return ResponseEntity.ok(Map.of("email", learner.getEmail(), "token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        return learnerRepository.findByEmail(email)
                .filter(l -> passwordEncoder.matches(password, l.getPasswordHash()))
                .map(l -> {
                    String token = jwtUtil.generateToken(email);
                    return ResponseEntity.ok(Map.of("token", token));
                })
                .orElse(ResponseEntity.status(401).build());
    }
}