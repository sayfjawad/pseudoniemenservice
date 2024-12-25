package nl.ictu.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.junit.jupiter.api.Test;

class AESHelperTest {

    @Test
    void generateIV_ShouldReturnGCMParameterSpec_WithNonNullIV() {
        // WHEN
        GCMParameterSpec gcmParameterSpec = AesUtility.generateIV();
        // THEN
        assertNotNull(gcmParameterSpec, "GCMParameterSpec should not be null");
        assertEquals(AesUtility.TAG_LENGTH, gcmParameterSpec.getTLen(),
                "Tag length should be 128 (bits)");
        // The IV array is extracted from gcmParameterSpec
        byte[] iv = gcmParameterSpec.getIV();
        assertNotNull(iv, "IV should not be null");
        assertEquals(AesUtility.IV_LENGTH, iv.length,
                "IV length should be " + AesUtility.IV_LENGTH);
    }

    @Test
    void createIVfromValues_ShouldReturnGCMParameterSpec_FromGivenIV() {
        // GIVEN
        byte[] ivSource = new byte[AesUtility.IV_LENGTH];
        // Fill the array with deterministic data for test
        for (int i = 0; i < ivSource.length; i++) {
            ivSource[i] = (byte) i;
        }
        // WHEN
        GCMParameterSpec spec = AesUtility.createIVfromValues(ivSource);
        // THEN
        assertNotNull(spec, "GCMParameterSpec should not be null");
        assertEquals(AesUtility.TAG_LENGTH, spec.getTLen(),
                "Tag length should be 128 (bits)");
        assertArrayEquals(ivSource, spec.getIV(),
                "IV array in GCMParameterSpec should match the input");
    }

    @Test
    void createCipher_ShouldReturnAesGcmNoPaddingCipher()
            throws NoSuchPaddingException, NoSuchAlgorithmException {
        // WHEN
        Cipher cipher = AesUtility.createCipher();
        // THEN
        assertNotNull(cipher, "Cipher should not be null");
        // Depending on the JVM/provider, the algorithm name can be uppercase or some variation,
        // but typically you'd expect "AES/GCM/NoPadding".
        assertEquals("AES/GCM/NoPadding", cipher.getAlgorithm(),
                "Cipher algorithm should match AES/GCM/NoPadding");
    }

    @Test
    void getAESEngine_ShouldReturnNonNullAESEngineInstance() {
        // WHEN
        MultiBlockCipher engine = AesUtility.getAESEngine();
        // THEN
        assertNotNull(engine, "Engine should not be null");
        assertInstanceOf(AESEngine.class, engine, "Engine should be an instance of AESEngine");
    }
}
