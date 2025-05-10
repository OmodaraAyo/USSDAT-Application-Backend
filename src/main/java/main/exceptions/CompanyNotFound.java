package main.exceptions;

public class CompanyNotFound extends RuntimeException {
    public CompanyNotFound(String message) {
        super(message);
    }
}
