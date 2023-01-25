package com.example.prac;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class AdvanceApplierTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("apply 동작 성공")
    void apply() {
        clearStudents();
        givenStudent(101, 1);
        givenStudent(102, 2);
        givenStudent(103, 3);


        AdvanceApplier applier = new AdvanceApplier(jdbcTemplate);

        Targets targets = new Targets(Arrays.asList(
                new User(101, 1),
                new User(102, 2),
                new User(103, 3)
        ));

        applier.apply(targets);
        assertStudentGrade(101, 2);
        assertStudentGrade(102, 3);
        assertStudentGrade(103, 4);
    }

    @Test
    @DisplayName("apply 결과 체크")
    void applyResult() {
        clearStudents();
        givenStudent(101, 1);
        givenStudent(102, 2);
        givenStudent(103, 3);


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

    private void assertStudentGrade(int id, int expectedGrade) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select stu_id,grade from stuinfo where stu_id=?", id);
        // ResultSet으로부터 레코드를 읽어오기 위해 next()메소드를 사용하는데 next()메소드는 읽어올 레코드가 있으면 true를 반환하고 없으면 false를 반환합니다.
        rs.next();
        assertThat(rs.getInt("grade")).isEqualTo(expectedGrade);
    }


    private void clearStudents() {
        jdbcTemplate.update("truncate table stuinfo");
    }

    private void givenStudent(int id, int grade) {
        jdbcTemplate.update("insert into stuinfo values(?,?)", id, grade);
    }


}
