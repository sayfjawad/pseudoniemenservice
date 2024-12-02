package nl.ictu.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.utils.ByteArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import static nl.ictu.service.AESHelper.IV_LENGTH;

/**
 * Advanced Encryption Standard  Galois/Counter Mode (AES-GCM).
 */

@SuppressWarnings("DesignForExtension")
@Service
public class AesGcmCryptographerImpl implements AesGcmCryptographer {

    //private SecretKey secretKey;

    private Base64.Encoder base64Encoder = Base64.getEncoder();

    private Base64.Decoder base64Decoder = Base64.getDecoder();

    private MessageDigest sha256Digest;

    private final PseudoniemenServiceProperties pseudoniemenServiceProperties;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    public AesGcmCryptographerImpl(final PseudoniemenServiceProperties pseudoniemenServicePropertiesArg) {

        pseudoniemenServiceProperties = pseudoniemenServicePropertiesArg;

        try {
            sha256Digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        if (!StringUtils.hasText(pseudoniemenServiceProperties.getTokenPrivateKey())) {
            throw new RuntimeException("Please set a private token key");
        }

    }

    @Override
    public String encrypt(final String plaintext, final String salt) throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

        final Cipher cipher = AESHelper.createCipher();

        final GCMParameterSpec gcmParameterSpec = AESHelper.generateIV();

        final SecretKey secretKey = createSecretKey(salt);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedWithIV = new byte[IV_LENGTH + ciphertext.length];

        System.arraycopy(gcmParameterSpec.getIV(), 0, encryptedWithIV, 0, IV_LENGTH);
        System.arraycopy(ciphertext, 0, encryptedWithIV, IV_LENGTH, ciphertext.length);

        return base64Encoder.encodeToString(encryptedWithIV);
    }

    private SecretKey createSecretKey(final String salt) {

        byte[] keyBytes = base64Decoder.decode(pseudoniemenServiceProperties.getTokenPrivateKey());

        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);

        byte[] salterSecretBytes = ByteArrayUtils.concat(keyBytes, saltBytes);

        byte[] key = sha256Digest.digest(salterSecretBytes);

        final SecretKey secretKey = new SecretKeySpec(key, "AES");

        return secretKey;
    }

    @Override
    public String decrypt(final String ciphertextWithIv, final String salt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        final Cipher cipher = AESHelper.createCipher();

        final byte[] encryptedWithIV = base64Decoder.decode(ciphertextWithIv);

        byte[] iv = Arrays.copyOfRange(encryptedWithIV, 0, IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(encryptedWithIV, IV_LENGTH, encryptedWithIV.length);

        final GCMParameterSpec gcmParameterSpec = AESHelper.createIVfromValues(iv);

        final SecretKey secretKey = createSecretKey(salt);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] decryptedText = cipher.doFinal(ciphertext);

        return new String(decryptedText, StandardCharsets.UTF_8);

    }

}
