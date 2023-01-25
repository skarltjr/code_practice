package com.example.prac.elements;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class GivenAssertHelper {
    private JdbcTemplate jdbcTemplate;

    public GivenAssertHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void clearStudents() {
        jdbcTemplate.update("truncate table stuinfo");
    }

    public void givenStudent(int id, int grade) {
        jdbcTemplate.update("insert into stuinfo values(?,?)", id, grade);
    }

    public void assertStudentGrade(int id, int expectedGrade) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select stu_id,grade from stuinfo where stu_id=?", id);
        // ResultSet으로부터 레코드를 읽어오기 위해 next()메소드를 사용하는데 next()메소드는 읽어올 레코드가 있으면 true를 반환하고 없으면 false를 반환합니다.
        rs.next();
        assertThat(rs.getInt("grade")).isEqualTo(expectedGrade);
    }
}
