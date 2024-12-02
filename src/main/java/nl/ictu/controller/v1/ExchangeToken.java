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
import nl.ictu.service.Cryptographer;
import nl.ictu.service.TokenConverter;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeToken implements ExchangeTokenApi, VersionOneController {

    private final Cryptographer cryptographer;

    private final TokenConverter tokenConverter;

    private final ObjectMapper objectMapper;

    private final Environment environment;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeTokenForIdentifier(final String callerOIN, final WsExchangeTokenForIdentifierRequest wsExchangeTokenForIdentifierRequest) {

        final String encodedToken = cryptographer.decrypt(wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);

        final Token token = tokenConverter.decode(encodedToken);

        if (environment.acceptsProfiles(Profiles.of("test"))) {
            log.info("Received token: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(token));
        }

        if (!callerOIN.equals(token.getRecipientOIN())) {
            throw new RuntimeException("Sink OIN not the same");
        }

        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        final WsIdentifier wsIdentifier = new WsIdentifier();

        if (StringUtils.hasText(token.getBsn())) {
            wsIdentifier.setType(BSN);
            wsIdentifier.setValue(token.getBsn());
        }

        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifier);

        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);

    }
}
