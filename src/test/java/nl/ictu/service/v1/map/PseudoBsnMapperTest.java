package nl.ictu.service.v1.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import nl.ictu.model.Identifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PseudoBsnMapper}.
 */
@ExtendWith(MockitoExtension.class)
class PseudoBsnMapperTest {

    @Mock
    private AesGcmSivCryptographer aesGcmSivCryptographer;
    @InjectMocks
    private PseudoBsnMapper pseudoBsnMapper;

    @Test
    void map_ShouldReturnDecryptedBsn_WhenDecryptionSucceeds() throws Exception {
        // GIVEN
        String pseudo = "someEncryptedString";
        String oin = "TEST_OIN";
        // Suppose the decrypted Identifier has BSN "123456789"
        Identifier decryptedIdentifier = Identifier.builder()
                .bsn("123456789")
                .build();
        when(aesGcmSivCryptographer.decrypt(pseudo, oin))
                .thenReturn(decryptedIdentifier);
        // WHEN
        WsExchangeIdentifierResponse response = pseudoBsnMapper.map(pseudo, oin);
        // THEN
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getIdentifier(), "Identifier should not be null");
        assertEquals(BSN, response.getIdentifier().getType(), "Type should be BSN");
        assertEquals("123456789", response.getIdentifier().getValue(),
                "Decrypted BSN value should match");
    }
}