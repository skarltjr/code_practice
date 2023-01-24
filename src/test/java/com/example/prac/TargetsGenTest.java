package com.example.prac;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TargetsGenTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void gen() {
        //given
        clearStudents();
        givenStudent(101, 1);
        givenStudent(102, 2);
        givenStudent(103, 3);

        TargetsGen targetsGen = new TargetsGen(jdbcTemplate);
        Targets targets = targetsGen.gen();
        assertThat(targets.getUsers()).hasSize(3);
        assertThat(targets.getUsers().stream().map(user -> user.getId()).collect(Collectors.toList())).containsAll(List.of(101, 102, 103));
        assertThat(targets.getUsers().stream().map(user -> user.getGrade()).collect(Collectors.toList())).containsAll(List.of(1, 2, 3));
    }

    private void clearStudents() {
        jdbcTemplate.update("truncate table stuinfo");
    }

    private void givenStudent(int id, int grade) {
        jdbcTemplate.update("insert into stuinfo values(?,?)", id, grade);
    }


}
