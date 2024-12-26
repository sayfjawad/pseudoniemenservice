package nl.ictu.service.validate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.ictu.model.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OINValidatorTest {

    private OINValidator oinValidator;

    @BeforeEach
    void setUp() {

        oinValidator = new OINValidator();
    }

    @Test
    @DisplayName("isValid() returns true when callerOIN matches token's recipientOIN")
    void isValid_ReturnsTrue_WhenOINsMatch() {
        // GIVEN
        String callerOIN = "TEST_OIN_123";
        Token token = Token.builder().recipientOIN("TEST_OIN_123").build();
        // WHEN
        boolean result = oinValidator.isValid(callerOIN, token);
        // THEN
        assertTrue(result, "Expected isValid() to return true for matching OINs");
    }

    @Test
    @DisplayName("isValid() returns false when callerOIN does not match token's recipientOIN")
    void isValid_ReturnsFalse_WhenOINsDoNotMatch() {
        // GIVEN
        String callerOIN = "TEST_OIN_ABC";
        Token token = Token.builder().recipientOIN("TEST_OIN_XYZ").build();
        // WHEN
        boolean result = oinValidator.isValid(callerOIN, token);
        // THEN
        assertFalse(result, "Expected isValid() to return false for non-matching OINs");
    }

    // OPTIONAL: If you want to test null behavior
    @Test
    @DisplayName("isValid() returns false or throws if token recipientOIN is null")
    void isValid_Behavior_WhenTokenRecipientOINIsNull() {
        // GIVEN
        String callerOIN = "NON_NULL_OIN";
        Token token = Token.builder().build();
        // WHEN
        // This will return false if callerOIN is not null,
        // or might throw NullPointerException if you rely on the equals contract
        boolean result = oinValidator.isValid(callerOIN, token);
        // THEN
        assertFalse(result, "Expected isValid() to return false if token's recipientOIN is null");
    }
}
