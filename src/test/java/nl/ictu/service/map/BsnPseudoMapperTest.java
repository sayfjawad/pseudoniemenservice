package nl.ictu.service.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import nl.ictu.crypto.AesGcmSivCryptographer;
import nl.ictu.model.Identifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BsnPseudoMapperTest {

    @Mock
    private AesGcmSivCryptographer aesGcmSivCryptographer;
    @InjectMocks
    private BsnPseudoMapper bsnPseudoMapper;

    @Test
    void map_ShouldReturnWsExchangeIdentifierResponse_WhenEncryptionSucceeds() throws Exception {
        // GIVEN
        String bsn = "123456789";
        String oin = "OIN_X";
        String encryptedValue = "encryptedBsn123";
        // Mock the cryptographer to return a known "encrypted" value
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(oin)))
                .thenReturn(encryptedValue);
        // WHEN
        WsExchangeIdentifierResponse response = bsnPseudoMapper.map(bsn, oin);
        // THEN
        assertNotNull(response);
        assertNotNull(response.getIdentifier());
        assertEquals(ORGANISATION_PSEUDO, response.getIdentifier().getType());
        assertEquals(encryptedValue, response.getIdentifier().getValue());
    }

    @Test
    void map_ShouldThrowIOException_WhenEncryptThrowsIOException() throws Exception {
        // GIVEN
        String bsn = "987654321";
        String oin = "OIN_IO";
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(oin)))
                .thenThrow(new IOException("Simulated I/O error"));
        // WHEN & THEN
        assertThrows(IOException.class, () -> bsnPseudoMapper.map(bsn, oin));
    }

    @Test
    void map_ShouldThrowInvalidCipherTextException_WhenEncryptThrowsInvalidCipherTextException()
            throws Exception {
        // GIVEN
        String bsn = "111222333";
        String oin = "OIN_CIPHER";
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(oin)))
                .thenThrow(new InvalidCipherTextException("Simulated cipher error"));
        // WHEN & THEN
        assertThrows(InvalidCipherTextException.class, () -> bsnPseudoMapper.map(bsn, oin));
    }
}
