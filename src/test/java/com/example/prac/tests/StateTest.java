package com.example.prac.tests;

import com.example.prac.elements.AdvanceState;
import com.example.prac.elements.States;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StateTest {
    // 1. state 먼저 구현해나가보자 with tdd
    // 참고로 maven -> target / gradle -> build에 프로젝트 구성 파일 저장
    String ownPath = "build/state";
    States states = new States(Paths.get(ownPath));

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Path.of(ownPath));
    }

    @Test
    void startWithNoStateFile() {
        AdvanceState state = states.get();
        assertThat(state).isNull();
    }

    @Test
    @DisplayName("상태를 저장해보자")
    void set() throws IOException {
        states.set(AdvanceState.GENERATING);
        List<String> lines = Files.readAllLines(Paths.get(ownPath));
        assertThat(lines.get(0)).isEqualTo(AdvanceState.GENERATING.name());
    }

    @Test
    @DisplayName("상태를 가져와보자")
    void get() throws IOException {
        FileCopyUtils.copy(AdvanceState.GENERATING.name(), new FileWriter(Path.of(ownPath).toFile()));

        AdvanceState state = states.get();
        assertThat(state).isEqualTo(AdvanceState.GENERATING);
    }

}
