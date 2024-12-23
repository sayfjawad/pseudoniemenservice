package nl.ictu.service.v1.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.Identifier;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.utils.AESHelper;
import nl.ictu.utils.Base64Wrapper;
import nl.ictu.utils.MessageDigestUtil;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.GCMSIVBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Service;

/**
 * Advanced Encryption Standard Galois/Counter Mode synthetic initialization vector.
 */

@Slf4j
@SuppressWarnings("DesignForExtension")
@Service
@RequiredArgsConstructor
public class AesGcmSivCryptographerImpl implements AesGcmSivCryptographer {

    public static final int MAC_SIZE = 128;
    private static final int NONCE_LENTH = 12;

    private final PseudoniemenServiceProperties pseudoniemenServiceProperties;
    private final MessageDigestUtil messageDigestUtil;
    private final IdentifierConverter identifierConverter;
    private final Base64Wrapper base64Wrapper;

    private AEADParameters createSecretKey(final String salt) {

        final var nonce16 = messageDigestUtil.getMessageDigestSha256()
                .digest(salt.getBytes(StandardCharsets.UTF_8));
        final var nonce12 = Arrays.copyOf(nonce16, NONCE_LENTH);
        final String identifierPrivateKey = pseudoniemenServiceProperties.getIdentifierPrivateKey();
        final KeyParameter keyParameter = new KeyParameter(
                base64Wrapper.decode(identifierPrivateKey));
        return new AEADParameters(keyParameter, MAC_SIZE, nonce12);

    }

    @Override
    public String encrypt(final Identifier identifier, final String salt)
            throws InvalidCipherTextException, IOException {

        final var plaintext = identifierConverter.encode(identifier);
        final var cipher = new GCMSIVBlockCipher(AESHelper.getAESEngine());
        cipher.init(true, createSecretKey(salt));
        final var plainTextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        final var ciphertext = new byte[cipher.getOutputSize(plainTextBytes.length)];
        final var outputLength = cipher.processBytes(plainTextBytes, 0, plainTextBytes.length,
                ciphertext, 0);
        cipher.doFinal(ciphertext, outputLength);
        cipher.reset();
        return base64Wrapper.encodeToString(ciphertext);

    }

    @Override
    public Identifier decrypt(final String ciphertextString, final String salt)
            throws InvalidCipherTextException, JsonProcessingException {

        final var cipher = new GCMSIVBlockCipher(AESHelper.getAESEngine());
        cipher.init(false, createSecretKey(salt));
        final var ciphertext = base64Wrapper.decode(ciphertextString);
        final var plaintext = new byte[cipher.getOutputSize(ciphertext.length)];
        final var outputLength = cipher.processBytes(ciphertext, 0, ciphertext.length, plaintext,
                0);
        cipher.doFinal(plaintext, outputLength);
        cipher.reset();
        final var encodedIdentifier = new String(plaintext, StandardCharsets.UTF_8);
        return identifierConverter.decode(encodedIdentifier);
    }
}


