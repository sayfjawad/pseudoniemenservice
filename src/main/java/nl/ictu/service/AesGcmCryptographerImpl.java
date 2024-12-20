package nl.ictu.service;

import static nl.ictu.service.AESHelper.IV_LENGTH;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.service.exception.TokenPrivateKeyException;
import nl.ictu.utils.Base64Wrapper;
import nl.ictu.utils.ByteArrayUtils;
import nl.ictu.utils.MessageDigestUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Advanced Encryption Standard  Galois/Counter Mode (AES-GCM).
 */

@SuppressWarnings("DesignForExtension")
@Service
public class AesGcmCryptographerImpl implements AesGcmCryptographer {

    private final Base64Wrapper base64Wrapper;

    private final MessageDigestUtil messageDigestUtil;

    private final PseudoniemenServiceProperties pseudoniemenServiceProperties;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    @SneakyThrows
    public AesGcmCryptographerImpl(
            final PseudoniemenServiceProperties properties,
            final Base64Wrapper base64,
            final MessageDigestUtil messageDigest) {

        this.pseudoniemenServiceProperties = properties;
        this.base64Wrapper = base64;
        this.messageDigestUtil = messageDigest;

        if (!StringUtils.hasText(pseudoniemenServiceProperties.getTokenPrivateKey())) {
            throw new TokenPrivateKeyException("Please set a private token key");
        }
    }

    @Override
    public String encrypt(final String plaintext, final String salt)
            throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

        final var cipher = AESHelper.createCipher();

        final var gcmParameterSpec = AESHelper.generateIV();

        final var secretKey = createSecretKey(salt);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        final var ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        final var gcmIV = gcmParameterSpec.getIV();
        final var encryptedWithIV = ByteArrayUtils.concat(gcmIV, ciphertext);

        return base64Wrapper.encodeToString(encryptedWithIV);
    }

    private SecretKey createSecretKey(final String salt) {

        final var keyBytes = base64Wrapper.decode(
                pseudoniemenServiceProperties.getTokenPrivateKey());

        final var saltBytes = salt.getBytes(StandardCharsets.UTF_8);

        final var salterSecretBytes = ByteArrayUtils.concat(keyBytes, saltBytes);

        final var key = messageDigestUtil.getMessageDigestSha256().digest(salterSecretBytes);

        return new SecretKeySpec(key, "AES");

    }

    @Override
    public String decrypt(final String ciphertextWithIv, final String salt)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        final var cipher = AESHelper.createCipher();

        final var encryptedWithIV = base64Wrapper.decode(ciphertextWithIv);

        final var iv = Arrays.copyOfRange(encryptedWithIV, 0, IV_LENGTH);
        final var ciphertext = Arrays.copyOfRange(encryptedWithIV, IV_LENGTH,
                encryptedWithIV.length);

        final var gcmParameterSpec = AESHelper.createIVfromValues(iv);

        final var secretKey = createSecretKey(salt);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        final var decryptedText = cipher.doFinal(ciphertext);

        return new String(decryptedText, StandardCharsets.UTF_8);

    }
}
