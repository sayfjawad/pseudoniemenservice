package nl.ictu.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.Identifier;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Class for tesing {@link AesGcmCryptographerImpl}
 */

@Slf4j
@ActiveProfiles("test")
public class TestAesGcmSivCryptographer {

    private final AesGcmSivCryptographer aesGcmSivCryptographer = new AesGcmSivCryptographerImpl(
        new PseudoniemenServiceProperties().setIdentifierPrivateKey("QTBtVEhLN3EwMHJ3QXN1ZUFqNzVrT3hDQTBIWWNIZTU="),
        new IdentifierConverterImpl(new ObjectMapper())
    );

    private final Set<String> testStrings = new HashSet<>(Arrays.asList("a", "bb", "dsv", "ghad", "dhaht", "uDg5Av", "d93fdvv", "dj83hzHo", "38iKawKv9", "dk(gkzm)Mh", "gjk)s3$g9cQ"));

    @Test
    public void testEncyptDecryptForDifferentStringLengths() {

        testStrings.forEach(plain -> {

            try {
                final Identifier identifier = new Identifier();
                identifier.setBsn(plain);

                final String crypted = aesGcmSivCryptographer.encrypt(identifier, "helloHowAreyo12345678");
                final Identifier actual = aesGcmSivCryptographer.decrypt(crypted, "helloHowAreyo12345678");
                assertThat(actual.getBsn()).isEqualTo(plain);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });

    }


    // Test to ensure ciphertext is different for the same plaintext due to IV randomness
    @Test
    public void testCiphertextIsTheSameForSamePlaintext() throws Exception {

        // The same plaintext message
        String plaintext = "This is a test message to ensure ciphertext is different!";

        final Identifier identifier = new Identifier();
        identifier.setBsn(plaintext);

        String encryptedMessage1 = aesGcmSivCryptographer.encrypt(identifier, "aniceSaltGorYu");
        String encryptedMessage2 = aesGcmSivCryptographer.encrypt(identifier, "aniceSaltGorYu");

        // Assert that the two ciphertexts are different
        assertThat(encryptedMessage1).isEqualTo(encryptedMessage2);
    }

}
