package nl.ictu.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMSIVBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Advanced Encryption Standard Galois/Counter Mode synthetic initialization vector.
 */

@Slf4j
@SuppressWarnings("DesignForExtension")
@Service
public class AesGcmSivCryptographerImpl implements AesGcmSivCryptographer {

    private final PseudoniemenServiceProperties pseudoniemenServiceProperties;

    private static final int NONCE_LENTH = 12;

    private Base64.Encoder base64Encoder = Base64.getEncoder();

    private Base64.Decoder base64Decoder = Base64.getDecoder();

    private final AESEngine aesEngine;

    private MessageDigest sha256Digest;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    public AesGcmSivCryptographerImpl(final PseudoniemenServiceProperties pseudoniemenServicePropertiesArg) {

        pseudoniemenServiceProperties = pseudoniemenServicePropertiesArg;

        aesEngine = new AESEngine();

        try {
            this.sha256Digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        if (!StringUtils.hasText(pseudoniemenServiceProperties.getIdentifierPrivateKey())) {
            throw new RuntimeException("Please set a private identifier key");
        }

    }

    private AEADParameters createSecretKey(final String salt) {

        final byte[] nonce16 = sha256Digest.digest(salt.getBytes(StandardCharsets.UTF_8));

        byte[] nonce12 = Arrays.copyOf(nonce16, NONCE_LENTH);

        final String identifierPrivateKey = pseudoniemenServiceProperties.getIdentifierPrivateKey();

        final KeyParameter keyParameter = new KeyParameter(base64Decoder.decode(identifierPrivateKey));

        log.info("keylength: " + keyParameter.getKeyLength());

        final AEADParameters cipherParameter = new AEADParameters(keyParameter, 128, nonce12);

        return cipherParameter;

    }

    @Override
    public String encrypt(final String plaintext, final String salt) throws InvalidCipherTextException {

        final GCMSIVBlockCipher cipher = new GCMSIVBlockCipher(aesEngine);

        cipher.init(true, createSecretKey(salt));

        final byte[] plainTextBytes = plaintext.getBytes(StandardCharsets.UTF_8);

        final byte[] ciphertext = new byte[cipher.getOutputSize(plainTextBytes.length)];

        final int outputLength = cipher.processBytes(plainTextBytes, 0, plainTextBytes.length, ciphertext, 0);

        cipher.doFinal(ciphertext, outputLength);

        cipher.reset();

        return base64Encoder.encodeToString(ciphertext);

    }

    @Override
    public String decrypt(final String ciphertextString, final String salt) throws InvalidCipherTextException {

        final GCMSIVBlockCipher cipher = new GCMSIVBlockCipher(aesEngine);

        cipher.init(false, createSecretKey(salt));

        final byte[] ciphertext = base64Decoder.decode(ciphertextString);

        final byte[] plaintext = new byte[cipher.getOutputSize(ciphertext.length)];

        final int outputLength = cipher.processBytes(ciphertext, 0, ciphertext.length, plaintext, 0);

        cipher.doFinal(plaintext, outputLength);

        cipher.reset();

        return new String(plaintext, StandardCharsets.UTF_8);

    }

}


