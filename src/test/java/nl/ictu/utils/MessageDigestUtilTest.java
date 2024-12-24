package nl.ictu.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.MessageDigest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageDigestUtilTest {

    private MessageDigestUtil messageDigestUtil;

    @BeforeEach
    void setUp() {
        messageDigestUtil = new MessageDigestUtil();
    }

    @Test
    void getMessageDigestSha256_ShouldReturnSha256Digest() {
        // WHEN
        MessageDigest digest = messageDigestUtil.getMessageDigestSha256();

        // THEN
        assertNotNull(digest, "MessageDigest should not be null");
        assertEquals("SHA-256", digest.getAlgorithm(),
                "Expected the digest algorithm to be SHA-256");
    }
}
