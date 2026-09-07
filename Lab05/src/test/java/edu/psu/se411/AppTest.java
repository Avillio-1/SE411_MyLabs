package edu.psu.se411;

import static org.junit.jupiter.api.Assertions.*;
import edu.psu.se411.exceptions.InvalidAgeException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AppTest {
    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 17})
    void rejectsUnderageValues(int age) {
        InvalidAgeException exception = assertThrowsExactly(
                InvalidAgeException.class, () -> App.validateAge(age));
        assertEquals("Age " + age + " is invalid; minimum age is 18", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {18, 19, 25, Integer.MAX_VALUE})
    void acceptsAdults(int age) {
        assertDoesNotThrow(() -> App.validateAge(age));
    }
}
