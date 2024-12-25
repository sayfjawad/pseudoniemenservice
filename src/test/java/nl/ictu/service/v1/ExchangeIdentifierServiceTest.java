package nl.ictu.service.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.service.exception.InvalidWsIdentifierRequestTypeException;
import nl.ictu.service.v1.map.BsnPseudoMapper;
import nl.ictu.service.v1.map.PseudoBsnMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ExchangeIdentifierService}.
 */
@ExtendWith(MockitoExtension.class)
class ExchangeIdentifierServiceTest {

    @Mock
    private BsnPseudoMapper bsnPseudoMapper;
    @Mock
    private PseudoBsnMapper pseudoBsnMapper;
    @InjectMocks
    private ExchangeIdentifierService exchangeIdentifierService;

    @Test
    @DisplayName("exchangeIdentifier() -> BSN -> ORGANISATION_PSEUDO path")
    void testExchangeIdentifier_BsnToOrgPseudo() throws Exception {
        // GIVEN
        var request = new WsExchangeIdentifierRequest()
                .identifier(new WsIdentifier().type(BSN).value("123456789"))
                .recipientOIN("TEST_OIN")
                .recipientIdentifierType(ORGANISATION_PSEUDO);
        // We mock BsnPseudoMapper to return a WsExchangeIdentifierResponse
        var mockedResponse = new WsExchangeIdentifierResponse();
        mockedResponse.setIdentifier(
                new WsIdentifier().type(ORGANISATION_PSEUDO).value("encryptedValue"));
        when(bsnPseudoMapper.map("123456789", "TEST_OIN")).thenReturn(mockedResponse);
        // WHEN
        WsExchangeIdentifierResponse actualResponse =
                exchangeIdentifierService.exchangeIdentifier("CALLER_OIN", request);
        // THEN
        // Verify the returned response is what the mapper gave back
        org.junit.jupiter.api.Assertions.assertNotNull(actualResponse);
        org.junit.jupiter.api.Assertions.assertNotNull(actualResponse.getIdentifier());
        org.junit.jupiter.api.Assertions.assertEquals(ORGANISATION_PSEUDO,
                actualResponse.getIdentifier().getType());
        org.junit.jupiter.api.Assertions.assertEquals("encryptedValue",
                actualResponse.getIdentifier().getValue());
    }

    @Test
    @DisplayName("exchangeIdentifier() -> ORGANISATION_PSEUDO -> BSN path")
    void testExchangeIdentifier_OrgPseudoToBsn() throws Exception {
        // GIVEN
        var request = new WsExchangeIdentifierRequest()
                .identifier(new WsIdentifier().type(ORGANISATION_PSEUDO).value("somePseudo"))
                .recipientOIN("TEST_OIN")
                .recipientIdentifierType(BSN);
        // We mock PseudoBsnMapper to return a WsExchangeIdentifierResponse
        var mockedResponse = new WsExchangeIdentifierResponse();
        mockedResponse.setIdentifier(new WsIdentifier().type(BSN).value("decryptedBsn"));
        when(pseudoBsnMapper.map("somePseudo", "TEST_OIN")).thenReturn(mockedResponse);
        // WHEN
        WsExchangeIdentifierResponse actualResponse =
                exchangeIdentifierService.exchangeIdentifier("CALLER_OIN", request);
        // THEN
        org.junit.jupiter.api.Assertions.assertNotNull(actualResponse);
        org.junit.jupiter.api.Assertions.assertNotNull(actualResponse.getIdentifier());
        org.junit.jupiter.api.Assertions.assertEquals(BSN,
                actualResponse.getIdentifier().getType());
        org.junit.jupiter.api.Assertions.assertEquals("decryptedBsn",
                actualResponse.getIdentifier().getValue());
    }

    @Test
    @DisplayName("exchangeIdentifier() -> throws InvalidWsIdentifierRequestTypeException for unsupported mappings")
    void testExchangeIdentifier_UnsupportedMapping_ThrowsException() {
        // GIVEN
        var request = new WsExchangeIdentifierRequest()
                // Let's say we do something like BSN -> BSN or ORG_PSEUDO -> ORG_PSEUDO
                .identifier(new WsIdentifier().type(BSN).value("12345"))
                .recipientOIN("TEST_OIN")
                .recipientIdentifierType(BSN);
        // WHEN & THEN
        assertThrows(
                InvalidWsIdentifierRequestTypeException.class,
                () -> exchangeIdentifierService.exchangeIdentifier("CALLER_OIN", request)
        );
    }
}
