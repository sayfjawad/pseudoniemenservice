package nl.ictu.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ByteArrayUtilsTest {

    @Test
    @DisplayName("concat() -> should concatenate two non-empty arrays")
    void concat_ShouldConcatenateTwoArrays() {
        // GIVEN
        byte[] a = {1, 2, 3};
        byte[] b = {4, 5, 6};
        byte[] expected = {1, 2, 3, 4, 5, 6};

        // WHEN
        byte[] result = ByteArrayUtils.concat(a, b);

        // THEN
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("concat() -> should return empty array if both inputs are empty")
    void concat_ShouldHandleTwoEmptyArrays() {
        // GIVEN
        byte[] a = {};
        byte[] b = {};
        byte[] expected = {};

        // WHEN
        byte[] result = ByteArrayUtils.concat(a, b);

        // THEN
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("concat() -> should handle empty array on either side")
    void concat_ShouldHandleOneEmptyArray() {
        // GIVEN
        byte[] a = {1, 2, 3};
        byte[] b = {};
        byte[] expected1 = {1, 2, 3};

        // WHEN
        byte[] result1 = ByteArrayUtils.concat(a, b);

        // THEN
        assertArrayEquals(expected1, result1);

        // GIVEN
        byte[] c = {};
        byte[] d = {4, 5, 6};
        byte[] expected2 = {4, 5, 6};

        // WHEN
        byte[] result2 = ByteArrayUtils.concat(c, d);

        // THEN
        assertArrayEquals(expected2, result2);
    }
}
