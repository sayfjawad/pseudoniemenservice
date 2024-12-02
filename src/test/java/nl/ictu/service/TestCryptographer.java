package nl.ictu.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestCryptographer {

    @Autowired
    private Cryptographer cryptographer;

    private Set<String> testStrings = new HashSet<>(Arrays.asList("a", "bb", "dsv", "ghad", "dhaht", "uDg5Av", "d93fdvv", "dj83hzHo", "38iKawKv9", "dk(gkzm)Mh", "gjk)s3$g9cQ"));

    @Test
    public void testDifferentStringLengths() {

        testStrings.forEach(plain -> {

            try {
                final String crypted = cryptographer.encrypt(plain, "helloHowAreyo");
                final String actual = cryptographer.decrypt(crypted, "helloHowAreyo");
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

        String encryptedMessage1 = cryptographer.encrypt(plaintext, "aniceSaltGorYu");
        String encryptedMessage2 = cryptographer.encrypt(plaintext, "aniceSaltGorYu");

        // Assert that the two ciphertexts are different
        assertThat(encryptedMessage1).isNotEqualTo(encryptedMessage2);
    }

}
