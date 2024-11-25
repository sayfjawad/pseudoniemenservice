package nl.ictu.controller.v1;

import nl.ictu.psuedoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeIdentifierForIdentifierRequest;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeTokenForIdentifier200Response;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifierTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeIdentifier implements ExchangeIdentifierApi, VersionOneController {


    @Override
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeIdentifierForIdentifier(final WsExchangeIdentifierForIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {

        final WsIdentifier wsIdentifier = new WsIdentifier()
            .identifierType(WsIdentifierTypes.BSN)
            .identifierValue("476288216");

        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        wsExchangeTokenForIdentifier200Response.identifier(wsIdentifier);

        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);
    }
}
