package ru.nsu.ostest.security.utils;

import org.apache.commons.text.RandomStringGenerator;

import java.security.SecureRandom;

/**
 * Класс для генерации паролей случайной длины от 8 до 15 символов.
 */
public class PasswordGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final RandomStringGenerator generator = new RandomStringGenerator.Builder()
            .withinRange(33, 126)
            .filteredBy(ch ->
                    Character.isLetter(ch) || Character.isDigit(ch) ||
                            "!@#$%^&*()-_=+[]{}|;:,.<>?".indexOf(ch) != -1)
            .build();


    public static String generatePassword() {
        int passwordLength = 8 + random.nextInt(8);
        return generator.generate(passwordLength);
    }
}
