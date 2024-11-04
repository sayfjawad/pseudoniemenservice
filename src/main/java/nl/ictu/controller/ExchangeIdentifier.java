package nl.ictu.controller;

import nl.ictu.psuedoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifierTypes;
import org.springframework.http.ResponseEntity;

public class ExchangeIdentifier implements ExchangeIdentifierApi {
    @Override
    public ResponseEntity<WsIdentifier> exchangeIdentifierForIdentifier(final WsIdentifier wsIdentifierArg) {
        final WsIdentifier wsIdentifier = new WsIdentifier()
            .identifierType(WsIdentifierTypes.BSN)
            .identifierValue("123456789");

        return ResponseEntity.ok(wsIdentifier);
    }
}
