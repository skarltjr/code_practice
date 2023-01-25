package com.example.prac.tests;

import com.example.prac.elements.GivenAssertHelper;
import com.example.prac.elements.Targets;
import com.example.prac.elements.TargetsGen;
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
    @Autowired
    GivenAssertHelper helper;

    @Test
    void gen() {
        //given
        helper.clearStudents();
        helper.givenStudent(101, 1);
        helper.givenStudent(102, 2);
        helper.givenStudent(103, 3);

        TargetsGen targetsGen = new TargetsGen(jdbcTemplate);
        Targets targets = targetsGen.gen();
        assertThat(targets.getUsers()).hasSize(3);
        assertThat(targets.getUsers().stream().map(user -> user.getId()).collect(Collectors.toList())).containsAll(List.of(101, 102, 103));
        assertThat(targets.getUsers().stream().map(user -> user.getGrade()).collect(Collectors.toList())).containsAll(List.of(1, 2, 3));
    }
}
