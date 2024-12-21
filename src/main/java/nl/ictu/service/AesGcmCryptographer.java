package nl.ictu.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public interface AesGcmCryptographer {

    String encrypt(String plaintext, String salt)
            throws IllegalBlockSizeException,
            BadPaddingException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            NoSuchPaddingException;

    String decrypt(String ciphertext, String salt)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException;
}
