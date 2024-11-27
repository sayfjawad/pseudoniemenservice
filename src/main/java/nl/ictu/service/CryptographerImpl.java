package nl.ictu.service;

import nl.ictu.configuration.PseudoniemenServiceProperties;
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
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import static nl.ictu.service.AESHelper.IV_LENGTH;

@SuppressWarnings("DesignForExtension")
@Service
public class CryptographerImpl implements Cryptographer {

    private SecretKey secretKey;

    static final Base64.Encoder BASE_64_ENCODER = Base64.getEncoder();

    static final Base64.Decoder BASE_64_DECODER = Base64.getDecoder();

    public CryptographerImpl(final PseudoniemenServiceProperties pseudoniemenServiceProperties) {

        if (!StringUtils.hasText(pseudoniemenServiceProperties.getTokenPrivateKey())) {
            throw new RuntimeException("Please set a private key");
        }

        secretKey = new SecretKeySpec(BASE_64_DECODER.decode(pseudoniemenServiceProperties.getTokenPrivateKey()), "AES");
        System.out.println("test: " + BASE_64_ENCODER.encodeToString(secretKey.getEncoded()));
    }

    @Override
    public String encrypt(final String plaintext) throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

        final Cipher cipher = AESHelper.createCipher();

        final GCMParameterSpec gcmParameterSpec = AESHelper.generateIV();

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedWithIV = new byte[IV_LENGTH + ciphertext.length];

        System.arraycopy(gcmParameterSpec.getIV(), 0, encryptedWithIV, 0, IV_LENGTH);
        System.arraycopy(ciphertext, 0, encryptedWithIV, IV_LENGTH, ciphertext.length);

        return BASE_64_ENCODER.encodeToString(encryptedWithIV);
    }

    @Override
    public String decrypt(final String ciphertextWithIv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        final Cipher cipher = AESHelper.createCipher();

        final byte[] encryptedWithIV = BASE_64_DECODER.decode(ciphertextWithIv);

        byte[] iv = Arrays.copyOfRange(encryptedWithIV, 0, IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(encryptedWithIV, IV_LENGTH, encryptedWithIV.length);

        final GCMParameterSpec gcmParameterSpec = AESHelper.createIVfromValues(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] decryptedText = cipher.doFinal(ciphertext);

        return new String(decryptedText, StandardCharsets.UTF_8);

    }

}
