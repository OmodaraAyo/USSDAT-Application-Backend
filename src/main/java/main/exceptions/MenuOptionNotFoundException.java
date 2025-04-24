package main.exceptions;

public class MenuOptionNotFoundException extends RuntimeException {
    public MenuOptionNotFoundException(String message) {
        super(message);
    }
}
