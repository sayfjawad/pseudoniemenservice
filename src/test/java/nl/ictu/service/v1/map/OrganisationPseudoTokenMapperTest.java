package nl.ictu.service.v1.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import nl.ictu.model.Identifier;
import nl.ictu.model.Token;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link OrganisationPseudoTokenMapper}.
 */
@ExtendWith(MockitoExtension.class)
class OrganisationPseudoTokenMapperTest {

    @Mock
    private AesGcmSivCryptographer aesGcmSivCryptographer;
    @InjectMocks
    private OrganisationPseudoTokenMapper organisationPseudoTokenMapper;

    @Test
    void map_ShouldReturnEncryptedTokenResponse_WhenEncryptionSucceeds() throws Exception {
        // GIVEN
        String callerOIN = "TEST_OIN";
        Token token = Token.builder().bsn("123456789").build();
        String encryptedValue = "encryptedBSN";
        // We mock the cryptographer to return a known "encrypted" value
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(callerOIN)))
                .thenReturn(encryptedValue);
        // WHEN
        WsExchangeTokenResponse response = organisationPseudoTokenMapper.map(callerOIN, token);
        // THEN
        assertEquals(ORGANISATION_PSEUDO, response.getIdentifier().getType());
        assertEquals(encryptedValue, response.getIdentifier().getValue());
    }

    @Test
    void map_ShouldThrowInvalidCipherTextException_WhenEncryptionFails() throws Exception {
        // GIVEN
        String callerOIN = "FAILING_OIN";
        Token token = Token.builder().bsn("987654321").build();
        // We mock an InvalidCipherTextException
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(callerOIN)))
                .thenThrow(new InvalidCipherTextException("Simulated cipher error"));
        // WHEN & THEN
        assertThrows(InvalidCipherTextException.class,
                () -> organisationPseudoTokenMapper.map(callerOIN, token));
    }

    @Test
    void map_ShouldThrowIOException_WhenEncryptionThrowsIOException() throws Exception {
        // GIVEN
        String callerOIN = "IO_EXCEPTION_OIN";
        Token token = Token.builder().bsn("555555555").build();
        // We mock an IOException
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(callerOIN)))
                .thenThrow(new IOException("Simulated I/O error"));
        // WHEN & THEN
        assertThrows(IOException.class,
                () -> organisationPseudoTokenMapper.map(callerOIN, token));
    }
}
