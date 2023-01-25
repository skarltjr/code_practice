package com.example.prac;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AdvanceApplier {
    private JdbcTemplate jdbcTemplate;

    public AdvanceApplier(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ApplyResult apply(Targets targets) {
        Map<Integer, Integer> gradeCountMap = new HashMap<>();

        targets.getUsers().forEach(user -> {
            int nextGrade = user.getGrade() + 1;
            jdbcTemplate.update("update stuinfo set grade = ? where stu_id = ?",
                    nextGrade, user.getId());

            gradeCountMap.put(nextGrade, gradeCountMap.getOrDefault(nextGrade, 0) + 1);
        });

        ApplyResult result = makeApplyResult(gradeCountMap);
        return result;
    }

    private ApplyResult makeApplyResult(Map<Integer, Integer> gradeCountMap) {
        List<GradeCount> gradeCounts = new ArrayList<>();

        for (Integer grade : gradeCountMap.keySet()) {
            Integer countPerGrade = gradeCountMap.get(grade);
            GradeCount gradeCount = new GradeCount(grade, countPerGrade);
            gradeCounts.add(gradeCount);
        }

        return new ApplyResult(gradeCounts);
    }


}
