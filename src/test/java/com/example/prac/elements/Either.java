package com.example.prac.elements;

public class Either<L, R> {
    private L leftValue;
    private R rightValue;

    public Either(L leftValue, R rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Either<>(value, null);
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Either<>(null, value);
    }

    public boolean isLeft() {
        return this.leftValue != null;
    }

    public L getLeftValue() {
        return leftValue;
    }

    public R getRightValue() {
        return rightValue;
    }
}
