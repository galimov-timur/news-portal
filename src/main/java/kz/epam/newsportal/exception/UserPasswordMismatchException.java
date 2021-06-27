package kz.epam.newsportal.exception;

public class UserPasswordMismatchException extends Exception {
    public UserPasswordMismatchException(String message) {
        super(message);
    }
}
