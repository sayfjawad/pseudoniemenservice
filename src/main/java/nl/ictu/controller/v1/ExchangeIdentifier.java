package nl.ictu.controller.v1;

import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierForIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenForIdentifier200Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@RestController
public final class ExchangeIdentifier implements ExchangeIdentifierApi, VersionOneController {

    @Override
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeIdentifierForIdentifier(final String callerOIN, final WsExchangeIdentifierForIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {



        return ResponseEntity.status(NOT_IMPLEMENTED).build();


    }
}
