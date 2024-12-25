package nl.ictu.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AesUtilityTest {

    @Test
    @DisplayName("generateIV() -> should return GCMParameterSpec with correct IV & tag length")
    void generateIV_ShouldReturnGCMParameterSpec() {
        // WHEN
        GCMParameterSpec spec = AesUtility.generateIV();
        // THEN
        assertNotNull(spec, "GCMParameterSpec should not be null");
        assertEquals(AesUtility.TAG_LENGTH, spec.getTLen(),
                "Tag length should be " + AesUtility.TAG_LENGTH + " bits");
        assertNotNull(spec.getIV(), "IV should not be null");
        assertEquals(AesUtility.IV_LENGTH, spec.getIV().length,
                "IV length should be " + AesUtility.IV_LENGTH);
    }

    @Test
    @DisplayName("createIVfromValues() -> should build GCMParameterSpec from provided IV")
    void createIVfromValues_ShouldReturnGCMParameterSpecFromGivenIV() {
        // GIVEN: a deterministic IV of length 12
        byte[] ivBytes = new byte[AesUtility.IV_LENGTH];
        for (int i = 0; i < ivBytes.length; i++) {
            ivBytes[i] = (byte) i;
        }
        // WHEN
        GCMParameterSpec spec = AesUtility.createIVfromValues(ivBytes);
        // THEN
        assertNotNull(spec, "GCMParameterSpec should not be null");
        assertEquals(AesUtility.TAG_LENGTH, spec.getTLen(),
                "Tag length should be " + AesUtility.TAG_LENGTH + " bits");
        assertArrayEquals(ivBytes, spec.getIV(),
                "IV in GCMParameterSpec should match the input array");
    }

    @Test
    @DisplayName("createCipher() -> should return Cipher for AES/GCM/NoPadding")
    void createCipher_ShouldReturnAesGcmNoPadding()
            throws NoSuchPaddingException, NoSuchAlgorithmException {
        // WHEN
        Cipher cipher = AesUtility.createCipher();
        // THEN
        assertNotNull(cipher, "Cipher should not be null");
        assertEquals("AES/GCM/NoPadding", cipher.getAlgorithm(),
                "Expected the cipher algorithm to be AES/GCM/NoPadding");
    }

    @Test
    @DisplayName("getAESEngine() -> should return instance of AESEngine")
    void getAESEngine_ShouldReturnAesEngine() {
        // WHEN
        MultiBlockCipher engine = AesUtility.getAESEngine();
        // THEN
        assertNotNull(engine, "Engine should not be null");
        assertTrue(engine instanceof AESEngine,
                "Engine should be an instance of AESEngine");
    }
}
