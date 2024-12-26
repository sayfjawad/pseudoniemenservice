package nl.ictu.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.ictu.service.exception.WsGetTokenProcessingException;
import nl.ictu.service.map.WsGetTokenResponseMapper;
import nl.ictu.service.map.WsIdentifierOinBsnMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetTokenServiceTest {

    private final String recipientOIN = "123456789";
    private final String bsn = "987654321";
    @Mock
    private WsIdentifierOinBsnMapper wsIdentifierOinBsnMapper;
    @Mock
    private WsGetTokenResponseMapper wsGetTokenResponseMapper;
    @InjectMocks
    private GetTokenService getTokenService;

    @BeforeEach
    void setUp() {
        // This section can be used to initialize common test data if needed
    }

    @Test
    @DisplayName("getWsGetTokenResponse() -> Valid input")
    void testGetWsGetTokenResponse_ValidInput() throws Exception {
        // GIVEN
        var identifier = WsIdentifier.builder()
                .type(WsIdentifierTypes.BSN)
                .value(bsn)
                .build();
        var expectedResponse = new WsGetTokenResponse();
        // Stubbing dependencies
        when(wsIdentifierOinBsnMapper.map(identifier, recipientOIN)).thenReturn(bsn);
        when(wsGetTokenResponseMapper.map(eq(bsn), anyLong(), eq(recipientOIN))).thenReturn(
                expectedResponse);
        // WHEN
        WsGetTokenResponse actualResponse =
                getTokenService.getWsGetTokenResponse(recipientOIN, identifier);
        // THEN
        verify(wsIdentifierOinBsnMapper).map(identifier, recipientOIN);
        verify(wsGetTokenResponseMapper).map(eq(bsn), anyLong(), eq(recipientOIN));
        org.junit.jupiter.api.Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("getWsGetTokenResponse() -> Unexpected error during processing")
    void testGetWsGetTokenResponse_UnexpectedError() {
        // GIVEN
        var identifier = WsIdentifier.builder()
                .type(WsIdentifierTypes.BSN)
                .value(bsn)
                .build();
        var exceptionMessage = "Unexpected processing error";
        // Stubbing dependencies
        when(wsIdentifierOinBsnMapper.map(identifier, recipientOIN)).thenThrow(
                new RuntimeException(exceptionMessage));
        // WHEN & THEN
        Exception exception = assertThrows(WsGetTokenProcessingException.class,
                () -> getTokenService.getWsGetTokenResponse(recipientOIN, identifier));
        // Assert exception message
        org.junit.jupiter.api.Assertions.assertEquals(exceptionMessage, exception.getMessage());
        verify(wsIdentifierOinBsnMapper).map(identifier, recipientOIN);
        verifyNoInteractions(wsGetTokenResponseMapper);
    }
}
