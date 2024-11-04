package nl.ictu.controller;

import nl.ictu.psuedoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifierTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeIdentifier implements ExchangeIdentifierApi {
    @Override
    public ResponseEntity<WsIdentifier> exchangeIdentifierForIdentifier(WsGetTokenRequest wsGetTokenRequest) {
        final WsIdentifier wsIdentifier = new WsIdentifier().identifierType(WsIdentifierTypes.BSN).identifierValue("476288216");

        return ResponseEntity.ok(wsIdentifier);
    }
}
