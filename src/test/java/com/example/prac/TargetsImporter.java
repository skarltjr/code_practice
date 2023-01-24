package com.example.prac;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class TargetsImporter {
    public Targets importTargets(Path path) {
        if (Files.notExists(path)) {
            throw new NoTargetsFileException();
        }

        try {
            List<String> lines = Files.readAllLines(path);
            List<User> users = lines.stream().map(line -> {
                String[] values = line.split("=");
                if (values.length !=2) throw new TargetsFileBadFormatException();
                return new User(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
            }).collect(Collectors.toList());

            return new Targets(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
