package com.albsig.stundenmanager.common.callbacks;

public class Result<T> {
    private final T value;
    private final Exception error;

    private Result(T value, Exception error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> error(Exception error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return value != null;
    }

    public T getValue() {
        return value;
    }

    public Exception getError() {
        return error;
    }
}
