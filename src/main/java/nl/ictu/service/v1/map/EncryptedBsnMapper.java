package nl.ictu.service.v1.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncryptedBsnMapper {

    private final AesGcmSivCryptographer aesGcmSivCryptographer;

    /**
     * Maps the encrypted business service number to its decrypted value using the given recipient OIN.
     *
     * @param bsnValue the encrypted business service number to be decrypted
     * @param recipientOIN the recipient OIN key used for decryption
     * @return the decrypted business service number
     * @throws InvalidCipherTextException if an error occurs during decryption
     * @throws JsonProcessingException if an error occurs during JSON processing
     */
    public String map(final String bsnValue, final String recipientOIN)
            throws InvalidCipherTextException, JsonProcessingException {

        final var decodedIdentifier = aesGcmSivCryptographer.decrypt(bsnValue, recipientOIN);
        return decodedIdentifier.getBsn();
    }
}
