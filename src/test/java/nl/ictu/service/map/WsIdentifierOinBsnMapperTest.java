package nl.ictu.service.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WsIdentifierOinBsnMapperTest {

    @Mock
    private EncryptedBsnMapper encryptedBsnMapper;
    @InjectMocks
    private WsIdentifierOinBsnMapper wsIdentifierOinBsnMapper;

    @Test
    @DisplayName("map() -> Returns BSN value directly for BSN type")
    void testMap_BsnType() {
        // GIVEN
        String bsnValue = "987654321";
        WsIdentifier identifier = new WsIdentifier().type(BSN).value(bsnValue);
        // WHEN
        String result = wsIdentifierOinBsnMapper.map(identifier, "123456789");
        // THEN
        assertEquals(bsnValue, result);
        verifyNoInteractions(encryptedBsnMapper); // Ensure EncryptedBsnMapper is not called
    }

    @Test
    @DisplayName("map() -> Returns encrypted value for ORGANISATION_PSEUDO type")
    void testMap_OrganisationPseudoType() {
        // GIVEN
        String bsnValue = "987654321";
        String recipientOIN = "123456789";
        String encryptedValue = "encrypted-value";
        WsIdentifier identifier = new WsIdentifier()
                .type(ORGANISATION_PSEUDO)
                .value(bsnValue);
        when(encryptedBsnMapper.map(bsnValue, recipientOIN)).thenReturn(encryptedValue);
        // WHEN
        String result = wsIdentifierOinBsnMapper.map(identifier, recipientOIN);
        // THEN
        assertEquals(encryptedValue, result);
        verify(encryptedBsnMapper).map(bsnValue,
                recipientOIN); // Ensure EncryptedBsnMapper is called
    }
}
