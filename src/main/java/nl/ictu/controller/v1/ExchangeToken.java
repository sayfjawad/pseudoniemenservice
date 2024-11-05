package nl.ictu.controller.v1;

import nl.ictu.psuedoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeTokenForIdentifier200Response;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeTokenForIdentifierRequest;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifierTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeToken implements ExchangeTokenApi, VersionOneController {

    @Override
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeTokenForIdentifier(final WsExchangeTokenForIdentifierRequest wsExchangeTokenForIdentifierRequest) {

        final WsIdentifier wsIdentifier = new WsIdentifier()
            .identifierType(WsIdentifierTypes.BSN)
            .identifierValue("123456789");

        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        wsExchangeTokenForIdentifier200Response.identifier(wsIdentifier);

        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);

    }
}
