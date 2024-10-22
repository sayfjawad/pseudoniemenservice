package nl.ictu.controller;

import nl.ictu.psuedoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifierTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeToken implements ExchangeTokenApi {
    @Override
    public ResponseEntity<WsIdentifier> exchangeTokenForIdentifier(final String token, final String body) {

        final WsIdentifier wsIdentifier = new WsIdentifier()
            .identifierType(WsIdentifierTypes.BSN)
            .identifierValue("123456789");

        return ResponseEntity.ok(wsIdentifier);

    }
}
