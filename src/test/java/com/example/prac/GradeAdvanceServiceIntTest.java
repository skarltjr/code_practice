package com.example.prac;

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

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(GradeAdvanceService.DEFAULT_TARGETS_FILE);
    }

    @Test
    @DisplayName("승급 apply 성공")
    void applySuccess() {
        AdvanceResult result = service.advance();
        assertThat(result).isEqualTo(AdvanceResult.SUCCESS);
    }

}
