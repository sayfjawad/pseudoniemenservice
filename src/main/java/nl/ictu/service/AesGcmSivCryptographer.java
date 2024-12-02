package nl.ictu.service;

import org.bouncycastle.crypto.InvalidCipherTextException;

public interface AesGcmSivCryptographer {

    String encrypt(String plaintext, String salt) throws InvalidCipherTextException;

    String decrypt(String ciphertext, String salt) throws InvalidCipherTextException;
}
