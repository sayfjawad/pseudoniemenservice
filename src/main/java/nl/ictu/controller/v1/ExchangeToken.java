package nl.ictu.controller.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.Identifier;
import nl.ictu.Token;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.service.AesGcmCryptographer;
import nl.ictu.service.AesGcmSivCryptographer;
import nl.ictu.service.TokenConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Een naam van een class hoort aan te geven wat de class doet! in dit geval is het een controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeToken implements ExchangeTokenApi, VersionOneController {

    private final AesGcmCryptographer aesGcmCryptographer;
    private final AesGcmSivCryptographer aesGcmSivCryptographer;
    private final TokenConverter tokenConverter;

    @Override
    /**
     * Throwables hebben een waarde en een functie en door @SneakyThrows komen ze niet tot hun waarde!!!
     *
     * Dit kan veel beter opgelost worden door een global exception handler van Spring Boot
     */
    @SneakyThrows
    public ResponseEntity<WsExchangeTokenResponse> exchangeToken(final String callerOIN,
            final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest) {
        /**
         * En controller hoort te beschrijven hoe informatie binnenkomt en of de input voldoet aan
         * de minimale eisen.
         *
         * In deze controller wordt teveel gedaan;
         * - converteren van input
         * - cryptographie
         * - foutafhandeling
         * - configuratie validate!!!
         *
         * Dit hoort in verschillende lagen door verschillende componenten gesplitst te worden om de
         * testbaarheid en aanpasbaarheid en analyseerbaarheid van de applicatie te bevorderen
         */
        final String encodedToken = aesGcmCryptographer.decrypt(
                wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);
        final Token token = tokenConverter.decode(encodedToken);
        if (!callerOIN.equals(token.getRecipientOIN())) {
            throw new RuntimeException("Sink OIN not the same");
        }
        final WsExchangeTokenResponse wsExchangeTokenForIdentifier200Response = new WsExchangeTokenResponse();
        final WsIdentifier wsIdentifier = new WsIdentifier();
        switch (wsExchangeTokenForIdentifierRequest.getIdentifierType()) {
            case BSN -> {
                wsIdentifier.setType(BSN);
                wsIdentifier.setValue(token.getBsn());
            }
            case ORGANISATION_PSEUDO -> {
                final Identifier identifier = new Identifier();
                identifier.setBsn(token.getBsn());
                final String encrypt = aesGcmSivCryptographer.encrypt(identifier, callerOIN);
                wsIdentifier.setType(ORGANISATION_PSEUDO);
                wsIdentifier.setValue(encrypt);
            }
            default -> {
                return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
            }
        }
        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifier);
        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);
    }
}
