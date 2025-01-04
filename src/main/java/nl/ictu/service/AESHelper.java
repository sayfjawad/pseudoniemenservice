package nl.ictu.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Deze class is geen servoce en hoort in een aparte package te worden gezet dat zijn categorie
 * en intentie duidelijk maakt
 */
public final class AESHelper {

    public static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private static final String CIPHER = "AES/GCM/NoPadding";
    /**
     * het woordje (new) maakt het testen hier bijna onmogelijk!
     * wrappen en injecteren zorgt ervoor dat je je code echt kan testen zonder SecureRandom() ook te
     * moeten testen
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Deze constructor kan ook met een lombok annotatie afgevangen worden
     * @UtilityClass of @NoArgsConstructor(access = Level.PRIVATE)
     */
    private AESHelper() {

    }

    /**
     * Dit soort classes horen een eigen unit test te hebben en niet een integratietest dat
     * onleesbaar wordt omdat het te groot is
     * @return
     */
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
