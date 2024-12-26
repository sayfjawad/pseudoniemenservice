package nl.ictu.service.map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.InvalidKeyException;
import nl.ictu.crypto.AesGcmCryptographer;
import nl.ictu.crypto.TokenCoder;
import nl.ictu.model.Token;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WsGetTokenResponseMapperTest {

    private final String bsn = "987654321";
    private final long creationDate = System.currentTimeMillis();
    private final String recipientOIN = "123456789";
    private final String encodedToken = "encoded-token";
    @Mock
    private AesGcmCryptographer aesGcmCryptographer;
    @Mock
    private TokenCoder tokenCoder;
    @InjectMocks
    private WsGetTokenResponseMapper wsGetTokenResponseMapper;

    @Test
    @DisplayName("map() -> Successfully maps parameters to WsGetTokenResponse")
    void testMap_Success() throws Exception {

        final var encryptedToken = "encrypted-token";
        // GIVEN
        Token token = Token.builder()
                .version(WsGetTokenResponseMapper.V_1)
                .bsn(bsn)
                .creationDate(creationDate)
                .recipientOIN(recipientOIN)
                .build();
        when(tokenCoder.encode(token)).thenReturn(encodedToken);
        when(aesGcmCryptographer.encrypt(encodedToken, recipientOIN)).thenReturn(encryptedToken);
        // WHEN
        WsGetTokenResponse response = wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN);
        // THEN
        verify(tokenCoder).encode(token);
        verify(aesGcmCryptographer).encrypt(encodedToken, recipientOIN);
        org.junit.jupiter.api.Assertions.assertEquals(encryptedToken, response.getToken());
    }

    @Test
    @DisplayName("map() -> Fails due to IOException during token encoding")
    void testMap_EncodingIOException() throws Exception {
        // GIVEN
        Token token = Token.builder()
                .version(WsGetTokenResponseMapper.V_1)
                .bsn(bsn)
                .creationDate(creationDate)
                .recipientOIN(recipientOIN)
                .build();
        when(tokenCoder.encode(token)).thenThrow(new IOException("Encoding failed"));
        // WHEN & THEN
        assertThrows(IOException.class,
                () -> wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN));
        verify(tokenCoder).encode(token);
        verifyNoInteractions(aesGcmCryptographer);
    }

    @Test
    @DisplayName("map() -> Fails due to encryption errors")
    void testMap_EncryptionError() throws Exception {
        // GIVEN
        Token token = Token.builder()
                .version(WsGetTokenResponseMapper.V_1)
                .bsn(bsn)
                .creationDate(creationDate)
                .recipientOIN(recipientOIN)
                .build();
        when(tokenCoder.encode(token)).thenReturn(encodedToken);
        when(aesGcmCryptographer.encrypt(encodedToken, recipientOIN))
                .thenThrow(new InvalidKeyException("Invalid encryption key"));
        // WHEN & THEN
        assertThrows(InvalidKeyException.class,
                () -> wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN));
        verify(tokenCoder).encode(token);
        verify(aesGcmCryptographer).encrypt(encodedToken, recipientOIN);
    }

    @Test
    @DisplayName("map() -> Fails due to unexpected runtime exceptions")
    void testMap_UnexpectedError() throws Exception {
        // GIVEN
        Token token = Token.builder()
                .version(WsGetTokenResponseMapper.V_1)
                .bsn(bsn)
                .creationDate(creationDate)
                .recipientOIN(recipientOIN)
                .build();
        when(tokenCoder.encode(token)).thenReturn(encodedToken);
        when(aesGcmCryptographer.encrypt(encodedToken, recipientOIN)).thenThrow(
                new RuntimeException("Unexpected error"));
        // WHEN & THEN
        assertThrows(RuntimeException.class,
                () -> wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN));
        verify(tokenCoder).encode(token);
        verify(aesGcmCryptographer).encrypt(encodedToken, recipientOIN);
    }
}
