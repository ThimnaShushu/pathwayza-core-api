package com.pathwayza.core_api.controller;

import com.pathwayza.core_api.model.Learner;
import com.pathwayza.core_api.model.LearnerSubject;
import com.pathwayza.core_api.repository.LearnerRepository;
import com.pathwayza.core_api.repository.LearnerSubjectRepository;
import com.pathwayza.core_api.security.JwtUtil;
import com.pathwayza.core_api.service.ApsCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/learner")
@RequiredArgsConstructor
public class LearnerController {

    private final LearnerRepository learnerRepository;
    private final LearnerSubjectRepository subjectRepository;
    private final ApsCalculator apsCalculator;
    private final JwtUtil jwtUtil;

    @PostMapping("/subjects")
    public ResponseEntity<?> saveSubjects(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody List<LearnerSubject> subjects) {

        String email = extractEmail(authHeader);
        Learner learner = learnerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Learner not found"));

        subjects.forEach(s -> s.setLearnerId(learner.getId()));
        subjectRepository.saveAll(subjects);

        int aps = apsCalculator.calculate(subjects);

        return ResponseEntity.ok(Map.of(
                "aps", aps,
                "subjects", subjects
        ));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @RequestHeader("Authorization") String authHeader) {

        String email = extractEmail(authHeader);
        Learner learner = learnerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Learner not found"));

        List<LearnerSubject> subjects = subjectRepository.findByLearnerId(learner.getId());
        int aps = apsCalculator.calculate(subjects);

        return ResponseEntity.ok(Map.of(
                "learner", learner,
                "subjects", subjects,
                "aps", aps
        ));
    }

    private String extractEmail(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.extractEmail(token);
    }
}