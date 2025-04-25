package main.exceptions;

public class EmptyItemException extends RuntimeException {
    public EmptyItemException(String message) {
        super(message);
    }
}
