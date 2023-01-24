package com.example.prac;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// tdd를 위해 구현전 테스트에서 사용할것들을 inner class로 만들어서 확인
// 이후 f6으로 상위 레벨로 이동
class States {
    private Path path;

    public States(Path path) {
        this.path = path;
    }

    public AdvanceState get() {
        if (!Files.exists(path)) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(path);
            return AdvanceState.valueOf(lines.get(0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(AdvanceState state) {
        try {
            Files.write(path, state.name().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
