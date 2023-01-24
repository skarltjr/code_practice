package com.example.prac;

public class User {
    private int id;
    private int grade;

    public User(int id, int grade) {
        this.id = id;
        this.grade = grade;
    }

    public int getId() {
        return this.id;
    }

    public int getGrade() {
        return this.grade;
    }
}
