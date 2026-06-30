package com.pathwayza.core_api.repository;

import com.pathwayza.core_api.model.LearnerSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LearnerSubjectRepository extends JpaRepository<LearnerSubject, UUID> {
    List<LearnerSubject> findByLearnerId(UUID learnerId);
}