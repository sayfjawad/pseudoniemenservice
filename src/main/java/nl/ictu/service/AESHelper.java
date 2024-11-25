package nl.ictu.service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class AESHelper {

    final private static int KEY_LENGTH = 256;

    final public static int IV_LENGTH = 12;

    final private static int TAG_LENGTH = 128;

    final private static String CIPHER = "AES/GCM/NoPadding";

    final private static SecureRandom secureRandom = new SecureRandom();

    // Method to generate a random AES key
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_LENGTH); // 128-bit AES encryption
        return keyGenerator.generateKey();
    }

    // Method to generate a random Initialization Vector (IV)
    public static GCMParameterSpec generateIV() {
        byte[] iv = new byte[IV_LENGTH]; // AES block size is 16 bytes
        secureRandom.nextBytes(iv);

        final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);

        return gcmParameterSpec;
    }

    public static GCMParameterSpec createIVfromValues(byte[] iv) {
        final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        return gcmParameterSpec;
    }

    public static Cipher createCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(CIPHER);
    }

}