package nl.ictu.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Class for tesing {@link CryptographerImpl}
 */

@Slf4j
@ActiveProfiles("test")
public class TestCryptographer {

    private CryptographerImpl cryptographer = new CryptographerImpl();

    private Set<String> testStrings = new HashSet<>(Arrays.asList("a", "bb", "dsv", "ghad", "dhaht", "uDg5Av", "d93fdvv", "dj83hzHo", "38iKawKv9", "dk(gkzm)Mh", "gjk)s3$g9cQ"));

    @Test
    public void test() {

        testStrings.forEach(plain -> {

            try {
                final String crypted = cryptographer.encrypt(plain);

                log.info("encrypted: " + crypted);

                final String actual = cryptographer.decrypt(crypted);
                assertThat(actual).isEqualTo(plain);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }

        });

    }


    // Test to ensure ciphertext is different for the same plaintext due to IV randomness
    @Test
    public void testCiphertextIsDifferentForSamePlaintext() throws Exception {

        // The same plaintext message
        String plaintext = "This is a test message to ensure ciphertext is different!";

        String encryptedMessage1 = cryptographer.encrypt(plaintext);
        String encryptedMessage2 = cryptographer.encrypt(plaintext);

        // Assert that the two ciphertexts are different
        assertThat(encryptedMessage1).isNotEqualTo(encryptedMessage2);
    }

}
