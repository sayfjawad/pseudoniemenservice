package nl.ictu.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;

public final class AESHelper {

    public static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private static final String CIPHER = "AES/GCM/NoPadding";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private AESHelper() {

    }

    // Method to generate a random Initialization Vector (IV)
    public static GCMParameterSpec generateIV() {

        byte[] iv = new byte[IV_LENGTH]; // AES block size is 16 bytes
        SECURE_RANDOM.nextBytes(iv);
        return new GCMParameterSpec(TAG_LENGTH, iv);
    }

    public static GCMParameterSpec createIVfromValues(final byte[] iv) {

        return new GCMParameterSpec(TAG_LENGTH, iv);
    }

    public static Cipher createCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {

        return Cipher.getInstance(CIPHER);
    }
}
