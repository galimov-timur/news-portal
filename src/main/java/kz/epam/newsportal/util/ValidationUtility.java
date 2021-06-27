package kz.epam.newsportal.util;

import kz.epam.newsportal.exception.*;
import org.springframework.stereotype.Service;

import java.util.regex.*;

@Service
public class ValidationUtility {

    private static final String PROPERTY_EXCEPTION_MSG_TEMPLATE = "%s should not be empty and maximum %d chars long";
    private static final String PASSWORD_MISMATCH_MSG = "Passwords should match";
    private static final String EMAIL_FORMAT_INVALID_MSG = "Email address is in the wrong format";

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

    public void validateEmail(String email) throws UserEmailInvalidException {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()) {
            throw new UserEmailInvalidException(EMAIL_FORMAT_INVALID_MSG);
        }
    }

    public void validatePasswordMatching(String password1, String password2) throws UserPasswordMismatchException {
        if(!password1.equals(password2)) {
            throw new UserPasswordMismatchException(PASSWORD_MISMATCH_MSG);
        }
    }

    public void validateStringProperty(String property, int maxLength, String propertyName) throws InvalidPropertyFormatException {
        if(property.isEmpty() || property.length() > maxLength) {
            String exceptionText = String.format(PROPERTY_EXCEPTION_MSG_TEMPLATE, propertyName, maxLength);
            throw new InvalidPropertyFormatException(exceptionText);
        }
    }
}
