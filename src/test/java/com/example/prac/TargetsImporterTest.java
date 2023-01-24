package com.example.prac;

import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TargetsImporterTest {
    TargetsImporter importer = new TargetsImporter();
    @Test
    void importTargetsWithNoFile() {
        assertThrows(NoTargetsFileException.class, () -> {
            importer.importTargets(Paths.get("build/none"));
        });
    }

    @Test
    void importTargetsWithBadFormat() throws IOException {
        FileCopyUtils.copy("1055\n1066",
                new FileWriter(Paths.get("build/tfile").toFile()));

        assertThrows(TargetsFileBadFormatException.class, () -> {
            Targets targets = importer.importTargets(Paths.get("build/tfile"));
        });
    }

    @Test
    void importTargets() throws IOException {
        FileCopyUtils.copy("105=5\n106=6",
                new FileWriter(Paths.get("build/tfile").toFile()));

        Targets targets = importer.importTargets(Paths.get("build/tfile"));
        List<User> users = targets.getUsers();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getId()).isEqualTo(105);
        assertThat(users.get(0).getGrade()).isEqualTo(5);
        assertThat(users.get(1).getId()).isEqualTo(106);
        assertThat(users.get(1).getGrade()).isEqualTo(6);
    }
}
