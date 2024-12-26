package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import nl.ictu.Identifier;
import org.bouncycastle.crypto.InvalidCipherTextException;
/**
 * Wat is de toegevoegde waarde van een interface als het maar een keer geimplementeerd wordt?
 */
public interface AesGcmSivCryptographer {

    String encrypt(Identifier identifier, String salt)
            throws InvalidCipherTextException, IOException;

    Identifier decrypt(String ciphertext, String salt)
            throws InvalidCipherTextException, JsonProcessingException;
}
