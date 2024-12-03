package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.ictu.Identifier;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.IOException;

public interface AesGcmSivCryptographer {

    String encrypt(Identifier identifier, String salt) throws InvalidCipherTextException, IOException;

    Identifier decrypt(String ciphertext, String salt) throws InvalidCipherTextException, JsonProcessingException;
}
