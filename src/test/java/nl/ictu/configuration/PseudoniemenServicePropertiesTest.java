package nl.ictu.configuration;

import nl.ictu.service.exception.IdentifierPrivateKeyException;
import nl.ictu.service.exception.TokenPrivateKeyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PseudoniemenServicePropertiesTest {

    @Test
    void validate_WhenTokenPrivateKeyIsEmpty_ThrowsTokenPrivateKeyException() {
        // GIVEN
        PseudoniemenServiceProperties props = new PseudoniemenServiceProperties()
                .setTokenPrivateKey("")
                .setIdentifierPrivateKey("someIdentifierKey");

        // WHEN & THEN
        assertThrows(TokenPrivateKeyException.class, props::validate);
    }

    @Test
    void validate_WhenIdentifierPrivateKeyIsEmpty_ThrowsIdentifierPrivateKeyException() {
        // GIVEN
        PseudoniemenServiceProperties props = new PseudoniemenServiceProperties()
                .setTokenPrivateKey("someTokenKey")
                .setIdentifierPrivateKey("");

        // WHEN & THEN
        assertThrows(IdentifierPrivateKeyException.class, props::validate);
    }

    @Test
    void validate_WhenBothKeysAreSet_NoExceptionIsThrown() {
        // GIVEN
        PseudoniemenServiceProperties props = new PseudoniemenServiceProperties()
                .setTokenPrivateKey("someTokenKey")
                .setIdentifierPrivateKey("someIdentifierKey");

        // WHEN & THEN
        assertDoesNotThrow(props::validate);
    }
}
