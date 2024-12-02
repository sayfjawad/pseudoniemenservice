package nl.ictu.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.Identifier;
import nl.ictu.Token;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenForIdentifier200Response;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenForIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.ictu.service.AesGcmCryptographer;
import nl.ictu.service.AesGcmSivCryptographer;
import nl.ictu.service.IdentifierConverter;
import nl.ictu.service.TokenConverter;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeToken implements ExchangeTokenApi, VersionOneController {

    private final AesGcmCryptographer aesGcmCryptographer;

    private final AesGcmSivCryptographer aesGcmSivCryptographer;

    private final TokenConverter tokenConverter;

    private final ObjectMapper objectMapper;

    private final Environment environment;

    private final IdentifierConverter identifierConverter;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeTokenForIdentifier(final String callerOIN, final WsExchangeTokenForIdentifierRequest wsExchangeTokenForIdentifierRequest) {

        final String encodedToken = aesGcmCryptographer.decrypt(wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);

        final Token token = tokenConverter.decode(encodedToken);

        if (environment.acceptsProfiles(Profiles.of("test"))) {
            log.info("Received token: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(token));
        }

        if (!callerOIN.equals(token.getRecipientOIN())) {
            throw new RuntimeException("Sink OIN not the same");
        }

        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        final WsIdentifier wsIdentifier = new WsIdentifier();

        switch (wsExchangeTokenForIdentifierRequest.getIdentifierType()) {
            case BSN -> {
                wsIdentifier.setType(BSN);
                wsIdentifier.setValue(token.getBsn());
            }
            case ORGANISATION_PSEUDO -> {

                final Identifier identifier = new Identifier();

                identifier.setBsn(token.getBsn());

                final String encode = identifierConverter.encode(identifier);

                final String encrypt = aesGcmSivCryptographer.encrypt(encode, callerOIN);

                wsIdentifier.setType(WsIdentifierTypes.ORGANISATION_PSEUDO);
                wsIdentifier.setValue(encrypt);

            }
            default -> {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }

        }


        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifier);

        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);

    }
}
