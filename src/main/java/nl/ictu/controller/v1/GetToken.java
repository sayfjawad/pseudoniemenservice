package nl.ictu.controller.v1;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.Identifier;
import nl.ictu.Token;
import nl.ictu.pseudoniemenservice.generated.server.api.GetTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.ictu.service.AesGcmCryptographer;
import nl.ictu.service.AesGcmSivCryptographer;
import nl.ictu.service.TokenConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


/**
 * Een naam van een class hoort aan te geven wat de class doet! in dit geval is het een controller
 */
@RestController
@RequiredArgsConstructor
public final class GetToken implements GetTokenApi, VersionOneController {

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
    public ResponseEntity<WsGetTokenResponse> getToken(final String callerOIN,
            final WsGetTokenRequest wsGetTokenRequest) {
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

        // check is callerOIN allowed to communicatie with sinkOIN <------- NL/ENG door elkaar
        final WsGetTokenResponse wsGetToken200Response = new WsGetTokenResponse();
        final Token token = new Token();
        token.setCreationDate(System.currentTimeMillis());
        token.setRecipientOIN(wsGetTokenRequest.getRecipientOIN());
        if (wsGetTokenRequest.getIdentifier() != null) {
            switch (wsGetTokenRequest.getIdentifier().getType()) {
                case BSN -> token.setBsn(wsGetTokenRequest.getIdentifier().getValue());
                case ORGANISATION_PSEUDO -> {
                    final String orgPseudoEncryptedString = wsGetTokenRequest.getIdentifier()
                            .getValue();
                    final Identifier decodedIdentifier = aesGcmSivCryptographer.decrypt(
                            orgPseudoEncryptedString, wsGetTokenRequest.getRecipientOIN());
                    token.setBsn(decodedIdentifier.getBsn());
                }
                default -> {
                    return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
                }
            }
        }
        final String plainTextToken = tokenConverter.encode(token);
        wsGetToken200Response.token(
                aesGcmCryptographer.encrypt(plainTextToken, wsGetTokenRequest.getRecipientOIN()));
        return ResponseEntity.ok(wsGetToken200Response);
    }
}
