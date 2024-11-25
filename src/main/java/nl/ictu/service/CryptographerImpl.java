package nl.ictu.service;

import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import static nl.ictu.service.AESHelper.IV_LENGTH;

@Service
public class CryptographerImpl implements Cryptographer {

    final static SecretKey secretKey;

    final static Base64.Encoder base64Encoder = Base64.getEncoder();

    final static Base64.Decoder base64Decoder = Base64.getDecoder();

    static {
        try {
            secretKey = AESHelper.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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

//        System.out.println("encrytedCyhperText enc:" + new String(new byteciphertext));

        return base64Encoder.encodeToString(encryptedWithIV);
    }

    @Override
    public String decrypt(final String ciphertextWithIv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        final Cipher cipher = AESHelper.createCipher();

        final byte[] encryptedWithIV = base64Decoder.decode(ciphertextWithIv);

        byte[] iv = Arrays.copyOfRange(encryptedWithIV, 0, IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(encryptedWithIV, IV_LENGTH, encryptedWithIV.length);

        final GCMParameterSpec gcmParameterSpec = AESHelper.createIVfromValues(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] decryptedText = cipher.doFinal(ciphertext);

        return new String(decryptedText);

    }

}
