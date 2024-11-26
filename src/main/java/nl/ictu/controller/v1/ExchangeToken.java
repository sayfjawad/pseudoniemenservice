package nl.ictu.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.Token;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenForIdentifier200Response;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenForIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.ictu.service.Cryptographer;
import nl.ictu.service.TokenConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeToken implements ExchangeTokenApi, VersionOneController {

    private final Cryptographer cryptographer;

    private final TokenConverter tokenConverter;

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeTokenForIdentifier(final String callerOIN, final WsExchangeTokenForIdentifierRequest wsExchangeTokenForIdentifierRequest) {

        final String encodedToken = cryptographer.decrypt(wsExchangeTokenForIdentifierRequest.getToken());

        final Token token = tokenConverter.decode(encodedToken);

        //log.info("Received token: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(token));

        if (!callerOIN.equals(token.getRecipientOIN())) {
            throw new RuntimeException("Sink OIN not the same");
        }

        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        final WsIdentifier wsIdentifier = new WsIdentifier();

        wsIdentifier.setType(WsIdentifierTypes.fromValue(token.getIdentifier().getType()));
        wsIdentifier.setValue(token.getIdentifier().getValue());

        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifier);

        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);

    }
}
