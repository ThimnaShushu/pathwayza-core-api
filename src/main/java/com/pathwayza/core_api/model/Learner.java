package com.pathwayza.core_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "learners")
@Data
public class Learner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private Integer grade;
    private String province;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}