package nl.ictu.controller.v1;

import nl.ictu.psuedoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeIdentifierForIdentifierRequest;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeTokenForIdentifier200Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@RestController
public class ExchangeIdentifier implements ExchangeIdentifierApi, VersionOneController {

    @Override
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeIdentifierForIdentifier(String callerOIN, WsExchangeIdentifierForIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {
        return ResponseEntity.status(NOT_IMPLEMENTED).build();
    }
}
