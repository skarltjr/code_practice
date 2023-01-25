package com.example.prac;

import java.util.List;

public class ApplyResult {
    private List<GradeCount> gradeCounts;

    public ApplyResult(List<GradeCount> gradeCounts) {
        this.gradeCounts = gradeCounts;
    }

    public List<GradeCount> getGradeCounts() {
        return gradeCounts;
    }
}
