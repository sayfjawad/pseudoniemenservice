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
import nl.ictu.service.TokenConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ExchangeToken implements ExchangeTokenApi, VersionOneController {

    private final Cryptographer cryptographer;

    private final TokenConverter tokenConverter;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeTokenForIdentifier(final String callerOIN, final WsExchangeTokenForIdentifierRequest wsExchangeTokenForIdentifierRequest) {

        final String encodedToken = cryptographer.decrypt(wsExchangeTokenForIdentifierRequest.getToken());

        log.info("Received token: " + encodedToken);

        final Token token = tokenConverter.decode(encodedToken);

        if (!callerOIN.equals(token.getRecipientOIN())) {
            throw new RuntimeException("Sink OIN not the same");
        }

        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        final WsIdentifier wsIdentifier = new WsIdentifier();

        wsIdentifier.setIdentifierType(WsIdentifierTypes.fromValue(token.getIdentifier().getType()));
        wsIdentifier.setIdentifierValue(token.getIdentifier().getValue());

        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifier);

        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);

    }
}
