package com.example.prac;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class TargetsExporter {
    public void export(Path path, Targets targets) {
        // 참고사항 : 여기서 try() 내부 statement안에 적는것들은 닫힘을 보장받는다.
        // 만약 bw를 statement 말고 다음줄에 적으면 닫힘 보장이 안된다 -> 결과 반영이 제대로 안된다.
        try (BufferedWriter bw = Files.newBufferedWriter(path)){
            List<User> users = targets.getUsers();
            for (User user : users) {
                bw.write(user.getId() + "="+user.getGrade());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
