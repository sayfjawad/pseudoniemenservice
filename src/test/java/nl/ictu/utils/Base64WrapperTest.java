package nl.ictu.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Base64WrapperTest {

    private Base64Wrapper base64Wrapper;

    @BeforeEach
    void setUp() {

        base64Wrapper = new Base64Wrapper();
    }

    @Test
    @DisplayName("encode() -> Should encode bytes to Base64 bytes")
    void encode_ShouldEncodeBytesToBase64Bytes() {
        // GIVEN
        byte[] input = "Hello".getBytes(StandardCharsets.UTF_8);
        // WHEN
        byte[] result = base64Wrapper.encode(input);
        // THEN
        String resultAsString = new String(result, StandardCharsets.UTF_8);
        assertEquals("SGVsbG8=", resultAsString,
                "Expected Base64 encoding of 'Hello' to be 'SGVsbG8='");
    }

    @Test
    @DisplayName("encodeToString() -> Should encode bytes to Base64 string")
    void encodeToString_ShouldEncodeBytesToBase64String() {
        // GIVEN
        byte[] input = "Hello".getBytes(StandardCharsets.UTF_8);
        // WHEN
        String base64String = base64Wrapper.encodeToString(input);
        // THEN
        assertEquals("SGVsbG8=", base64String,
                "Expected Base64 encoding of 'Hello' to be 'SGVsbG8='");
    }

    @Test
    @DisplayName("decode() -> Should decode Base64 string to bytes")
    void decode_ShouldDecodeBase64StringToBytes() {
        // GIVEN
        String base64String = "SGVsbG8=";
        // WHEN
        byte[] decoded = base64Wrapper.decode(base64String);
        // THEN
        String decodedAsString = new String(decoded, StandardCharsets.UTF_8);
        assertEquals("Hello", decodedAsString,
                "Expected Base64 decoding of 'SGVsbG8=' to be 'Hello'");
    }

    @Test
    @DisplayName("decode() -> Should throw IllegalArgumentException on invalid Base64")
    void decode_ShouldThrowException_WhenInvalidBase64String() {
        // GIVEN
        String invalidBase64 = "Not valid base64!!!";
        // WHEN & THEN
        // Base64.getDecoder().decode(...) throws IllegalArgumentException on invalid input
        assertThrows(IllegalArgumentException.class,
                () -> base64Wrapper.decode(invalidBase64),
                "Expected decode() to throw IllegalArgumentException for invalid Base64 string");
    }
}
