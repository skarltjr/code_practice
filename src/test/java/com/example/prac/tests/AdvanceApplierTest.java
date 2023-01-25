package com.example.prac.tests;

import com.example.prac.elements.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class AdvanceApplierTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    GivenAssertHelper helper;

    @Test
    @DisplayName("apply 동작 성공")
    void apply() {
        helper.clearStudents();
        helper.givenStudent(101, 1);
        helper.givenStudent(102, 2);
        helper.givenStudent(103, 3);


        AdvanceApplier applier = new AdvanceApplier(jdbcTemplate);

        Targets targets = new Targets(Arrays.asList(
                new User(101, 1),
                new User(102, 2),
                new User(103, 3)
        ));

        applier.apply(targets);
        helper.assertStudentGrade(101, 2);
        helper.assertStudentGrade(102, 3);
        helper.assertStudentGrade(103, 4);
    }

    @Test
    @DisplayName("apply 결과 체크")
    void applyResult() {
        helper.clearStudents();
        helper.givenStudent(101, 1);
        helper.givenStudent(102, 2);
        helper.givenStudent(103, 3);


        AdvanceApplier applier = new AdvanceApplier(jdbcTemplate);

        User first = new User(101, 1);
        User second = new User(102, 2);
        Targets targets = new Targets(Arrays.asList(
                first,
                second
        ));

        ApplyResult applyResult = applier.apply(targets);
        List<GradeCount> gradeCounts = applyResult.getGradeCounts();
        assertThat(gradeCounts).contains(new GradeCount(2, 1), new GradeCount(3, 1));
    }
}
