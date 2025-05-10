package main.exceptions;

public class InvalidRequest extends IllegalArgumentException {
    public InvalidRequest(String message) {
        super(message);
    }
}
