package kz.epam.newsportal.util;

import kz.epam.newsportal.exception.InvalidPropertyFormatException;
import kz.epam.newsportal.exception.UserEmailInvalidException;
import kz.epam.newsportal.exception.UserPasswordMismatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilityTest {
    @Test
    void validateEmailTest() {
        ValidationUtility validationUtility = new ValidationUtility();
        String email = "testtest.ru";
        assertThrows(UserEmailInvalidException.class, ()->validationUtility.validateEmail(email), "Email validation");
    }

    @Test
    void validatePasswordMatchingTest() {
        ValidationUtility validationUtility = new ValidationUtility();
        String pass1 = "test";
        String pass2 = "testt";
        String pass3 = "test";
        assertThrows(UserPasswordMismatchException.class, ()->validationUtility.validatePasswordMatching(pass1,pass2), "Different password assertion");
        assertDoesNotThrow(()->validationUtility.validatePasswordMatching(pass1,pass3));
    }

    @ParameterizedTest
    @ValueSource(strings = {"dsasd123d", "aasdda", "asdsdddd", "asaaaadddddddddddddddddd", "asdfd", ""})
    void validatePropertyThrowsTest(String property) {
        int maxLength = 4;
        ValidationUtility validationUtility = new ValidationUtility();
        assertThrows(InvalidPropertyFormatException.class, ()->validationUtility.validateStringProperty(property, maxLength, property));
    }

    @ParameterizedTest
    @ValueSource(strings = {"dsd", "dsss", "ad", "a"})
    void validatePropertyDoesntThrowTest(String property) {
        int maxLength = 4;
        ValidationUtility validationUtility = new ValidationUtility();
        assertDoesNotThrow(()->validationUtility.validateStringProperty(property, maxLength, property));
    }
}
