package com.pathwayza.core_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "learner_subjects")
@Data
public class LearnerSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "learner_id")
    private UUID learnerId;

    private String subject;
    private String level;
    private Integer mark;
}