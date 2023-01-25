package com.example.prac.tests;

import com.example.prac.elements.AdvanceResult;
import com.example.prac.elements.GivenAssertHelper;
import com.example.prac.elements.GradeAdvanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.nio.file.Files;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class GradeAdvanceServiceIntTest {
    @Autowired
    GradeAdvanceService service;
    @Autowired
    GivenAssertHelper helper;



    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(GradeAdvanceService.DEFAULT_TARGETS_FILE);
    }

    @Test
    @DisplayName("승급 apply 성공")
    void applySuccess() {
        helper.clearStudents();
        helper.givenStudent(501, 1);
        helper.givenStudent(502, 2);

        AdvanceResult result = service.advance();
        assertThat(result).isEqualTo(AdvanceResult.SUCCESS);

        helper.assertStudentGrade(501,2);
        helper.assertStudentGrade(502,3);
    }

}
