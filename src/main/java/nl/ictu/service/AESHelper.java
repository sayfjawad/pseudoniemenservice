package nl.ictu.service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class AESHelper {

    private AESHelper() {
    }

    public static final int IV_LENGTH = 12;

    private static final int TAG_LENGTH = 128;

    private static final String CIPHER = "AES/GCM/NoPadding";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Method to generate a random Initialization Vector (IV)
    public static GCMParameterSpec generateIV() {
        byte[] iv = new byte[IV_LENGTH]; // AES block size is 16 bytes
        SECURE_RANDOM.nextBytes(iv);

        final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);

        return gcmParameterSpec;
    }

    public static GCMParameterSpec createIVfromValues(final byte[] iv) {
        final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        return gcmParameterSpec;
    }

    public static Cipher createCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(CIPHER);
    }

}
