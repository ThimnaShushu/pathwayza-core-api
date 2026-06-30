package com.pathwayza.core_api.service;

import com.pathwayza.core_api.model.LearnerSubject;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ApsCalculator {

    public int calculate(List<LearnerSubject> subjects) {
        int total = 0;
        for (LearnerSubject s : subjects) {
            total += convertMarkToApsPoint(s.getMark());
        }
        return total;
    }

    private int convertMarkToApsPoint(int mark) {
        if (mark >= 80) return 7;
        if (mark >= 70) return 6;
        if (mark >= 60) return 5;
        if (mark >= 50) return 4;
        if (mark >= 40) return 3;
        if (mark >= 30) return 2;
        return 1;
    }
}