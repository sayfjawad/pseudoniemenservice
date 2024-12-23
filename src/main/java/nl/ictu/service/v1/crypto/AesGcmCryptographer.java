package nl.ictu.service.v1.crypto;

import static nl.ictu.utils.AESHelper.IV_LENGTH;

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
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.utils.AESHelper;
import nl.ictu.utils.Base64Wrapper;
import nl.ictu.utils.ByteArrayUtils;
import nl.ictu.utils.MessageDigestUtil;
import org.springframework.stereotype.Service;

/**
 * Advanced Encryption Standard  Galois/Counter Mode (AES-GCM).
 */

@SuppressWarnings("DesignForExtension")
@Service
@RequiredArgsConstructor
public class AesGcmCryptographer {

    private final Base64Wrapper base64Wrapper;
    private final MessageDigestUtil messageDigestUtil;
    private final PseudoniemenServiceProperties pseudoniemenServiceProperties;

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
