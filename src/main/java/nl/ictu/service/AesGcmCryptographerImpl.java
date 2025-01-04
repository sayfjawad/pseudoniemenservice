package nl.ictu.service;

import static nl.ictu.service.AESHelper.IV_LENGTH;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.utils.ByteArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Advanced Encryption Standard  Galois/Counter Mode (AES-GCM).
 */

/**
 * Regels die overal genegeerd worden kunnen uitgezet of opgelost worden!
 */
@SuppressWarnings("DesignForExtension")
@Service
/**
 * Is dit een service? utility? converter? mapper? of all of the above?
 */
public class AesGcmCryptographerImpl implements AesGcmCryptographer {

    /**
     * Instantieren en final maken van dit soort standaard utilities kunnen beter in een wrapper
     * object gezet worden om mocking mogelijk te maken, op zijn maakt de wrapper object de
     * testbaarheid van de code beter door de features te isoleren i.p.v. te integreren
     */
    //private SecretKey secretKey;
    private final Base64.Encoder base64Encoder = Base64.getEncoder();
    private final Base64.Decoder base64Decoder = Base64.getDecoder();
    private final MessageDigest sha256Digest;
    private final PseudoniemenServiceProperties pseudoniemenServiceProperties;

    /**
     * Regels die overal genegeerd worden kunnen uitgezet of opgelost worden!
     */
    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    /**
     * @SneakyThrows zoals het hier gebruikt wordt kan voor problemen zorgen omdat het elders niet
     * goed afgevangen wordt.
     */
    @SneakyThrows
    public AesGcmCryptographerImpl(
            final PseudoniemenServiceProperties pseudoniemenServicePropertiesArg) {

        pseudoniemenServiceProperties = pseudoniemenServicePropertiesArg;
        sha256Digest = MessageDigest.getInstance("SHA-256");
        if (!StringUtils.hasText(pseudoniemenServiceProperties.getTokenPrivateKey())) {
            throw new RuntimeException("Please set a private token key");
        }
    }

    @Override
    public String encrypt(final String plaintext, final String salt)
            throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

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
        return new SecretKeySpec(key, "AES");
    }

    @Override
    public String decrypt(final String ciphertextWithIv, final String salt)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

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
