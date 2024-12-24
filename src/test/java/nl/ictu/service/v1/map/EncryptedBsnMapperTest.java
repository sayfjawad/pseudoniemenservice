package nl.ictu.service.v1.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import nl.ictu.model.Identifier;
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EncryptedBsnMapperTest {

    @Mock
    private AesGcmSivCryptographer aesGcmSivCryptographer;
    @InjectMocks
    private EncryptedBsnMapper encryptedBsnMapper;

    @Test
    void map_ShouldReturnDecryptedBsn_WhenDecryptSucceeds() {
        // GIVEN
        String encryptedBsn = "someEncryptedValue";
        String recipientOin = "testOIN";
        // Suppose decrypt returns an Identifier with "123456789" as BSN
        Identifier decryptedIdentifier = Identifier.builder()
                .bsn("123456789")
                .build();
        when(aesGcmSivCryptographer.decrypt(encryptedBsn, recipientOin))
                .thenReturn(decryptedIdentifier);
        // WHEN
        String result = encryptedBsnMapper.map(encryptedBsn, recipientOin);
        // THEN
        assertEquals("123456789", result);
    }
}
