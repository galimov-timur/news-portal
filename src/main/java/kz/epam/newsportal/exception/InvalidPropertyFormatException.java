package kz.epam.newsportal.exception;

public class InvalidPropertyFormatException extends Exception {
    public InvalidPropertyFormatException(String message) {
        super("Invalid property: " + message);
    }
}
