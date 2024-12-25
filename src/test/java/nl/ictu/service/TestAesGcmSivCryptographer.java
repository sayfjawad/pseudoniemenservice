package nl.ictu.service;


import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.model.Identifier;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.service.v1.crypto.AesGcmCryptographer;
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import nl.ictu.service.v1.crypto.IdentifierConverter;
import nl.ictu.utils.Base64Wrapper;
import nl.ictu.utils.MessageDigestWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

/**
 * Class for tesing {@link AesGcmCryptographer}
 */

@Slf4j
@ActiveProfiles("test")
class TestAesGcmSivCryptographer {

    private final AesGcmSivCryptographer aesGcmSivCryptographer = new AesGcmSivCryptographer(
            new PseudoniemenServiceProperties().setIdentifierPrivateKey(
                    "QTBtVEhLN3EwMHJ3QXN1ZUFqNzVrT3hDQTBIWWNIZTU="),
            new MessageDigestWrapper(),
            new IdentifierConverter(new ObjectMapper()),
            new Base64Wrapper()
    );

    private final Set<String> testStrings = new HashSet<>(
            Arrays.asList("a", "bb", "dsv", "ghad", "dhaht", "uDg5Av", "d93fdvv", "dj83hzHo",
                    "38iKawKv9", "dk(gkzm)Mh", "gjk)s3$g9cQ"));

    @Test
    void testEncyptDecryptForDifferentStringLengths() {

        testStrings.forEach(plain -> {

            try {
                final String crypted = aesGcmSivCryptographer.encrypt(Identifier.builder()
                                .bsn(plain)
                                .build(),
                        "helloHowAreyo12345678");
                final Identifier actual = aesGcmSivCryptographer.decrypt(crypted,
                        "helloHowAreyo12345678");
                assertThat(actual.getBsn()).isEqualTo(plain);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });

    }


    // Test to ensure ciphertext is different for the same plaintext due to IV randomness
    @Test
    void testCiphertextIsTheSameForSamePlaintext() throws Exception {

        // The same plaintext message
        final var plaintext = "This is a test message to ensure ciphertext is different!";
        final var identifier = Identifier.builder().bsn(plaintext).build();

        final var encryptedMessage1 = aesGcmSivCryptographer.encrypt(identifier, "aniceSaltGorYu");
        final var encryptedMessage2 = aesGcmSivCryptographer.encrypt(identifier, "aniceSaltGorYu");

        // Assert that the two ciphertexts are different
        assertThat(encryptedMessage1).isEqualTo(encryptedMessage2);
    }

}
