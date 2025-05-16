package main.exceptions;

public class CustomUssdException extends Exception {
    private boolean isEnd;

    public CustomUssdException(String message, boolean isEnd) {
        super(message);
        this.isEnd = isEnd;
    }

    public boolean isEnd() {
        return isEnd;
    }
}