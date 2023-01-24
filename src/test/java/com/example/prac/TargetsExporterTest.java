package com.example.prac;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TargetsExporterTest {
    @Test
    void export() throws IOException {
        TargetsExporter exporter = new TargetsExporter();
        Path path = Paths.get("build/studentsList");
        exporter.export(path,
                new Targets(Arrays.asList(
                        new User(101,1),
                        new User(102,2)
                )));

        List<String> lines = Files.readAllLines(path);
        assertThat(lines.get(0)).isEqualTo("101=1");
        assertThat(lines.get(1)).isEqualTo("102=2");
    }
}
