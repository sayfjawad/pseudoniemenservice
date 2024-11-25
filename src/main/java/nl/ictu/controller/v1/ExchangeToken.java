package nl.ictu.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.Token;
import nl.ictu.psuedoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeTokenForIdentifier200Response;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeTokenForIdentifierRequest;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.ictu.service.Cryptographer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ExchangeToken implements ExchangeTokenApi, VersionOneController {

    private final Cryptographer cryptographer;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeTokenForIdentifier(final String callerOIN, final WsExchangeTokenForIdentifierRequest wsExchangeTokenForIdentifierRequest) {

        final String encodedToken = cryptographer.decrypt(wsExchangeTokenForIdentifierRequest.getToken());

        final Token token = TokenHelper.decode(encodedToken);

        log.info("Received token: " + token.toString());

        if (!callerOIN.equals(token.getSinkOIN())) {
            throw new RuntimeException("Sink OIN not the same");
        }

        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        final WsIdentifier wsIdentifier = new WsIdentifier();

        wsIdentifier.setIdentifierType(WsIdentifierTypes.fromValue(token.getIdentifierType()));
        wsIdentifier.setIdentifierValue(token.getIdentifierValue());

        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifier);

        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);

    }
}
