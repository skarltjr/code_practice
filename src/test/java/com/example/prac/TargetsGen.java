package com.example.prac;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TargetsGen {
    private JdbcTemplate jdbcTemplate;

    public TargetsGen(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Targets gen() {
        List<User> users = jdbcTemplate.query("select * from stuinfo",
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new User(rs.getInt("stu_id"), rs.getInt("grade"));
                    }
                });
        return new Targets(users);
    }
}
