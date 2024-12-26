package nl.ictu.controller.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.Identifier;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.ictu.service.AesGcmSivCryptographer;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Een naam van een class hoort aan te geven wat de class doet! in dit geval is het een controller
 */
@RequiredArgsConstructor
@RestController
public final class ExchangeIdentifier implements ExchangeIdentifierApi, VersionOneController {

    private final AesGcmSivCryptographer aesGcmSivCryptographer;

    @Override
    /**
     * Throwables hebben een waarde en een functie en door @SneakyThrows komen ze niet tot hun waarde!!!
     *
     * Dit kan veel beter opgelost worden door een global exception handler van Spring Boot
     */
    @SneakyThrows
    public ResponseEntity<WsExchangeIdentifierResponse> exchangeIdentifier(final String callerOIN,
            final WsExchangeIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {

        final WsIdentifier wsIdentifierRequest = wsExchangeIdentifierForIdentifierRequest.getIdentifier();
        final String recipientOIN = wsExchangeIdentifierForIdentifierRequest.getRecipientOIN();
        final WsIdentifierTypes recipientIdentifierType = wsExchangeIdentifierForIdentifierRequest.getRecipientIdentifierType();
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
        if (BSN.equals(wsIdentifierRequest.getType()) && ORGANISATION_PSEUDO.equals(
                recipientIdentifierType)) {
            // from BSN to Org Pseudo
            return ResponseEntity.ok(
                    convertBsnToPseudo(wsIdentifierRequest.getValue(), recipientOIN));
        } else if (ORGANISATION_PSEUDO.equals(wsIdentifierRequest.getType()) && BSN.equals(
                recipientIdentifierType)) {
            // from BSN to Org Pseudo
            return ResponseEntity.ok(
                    convertPseudoToBEsn(wsIdentifierRequest.getValue(), recipientOIN));
        } else {
            return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
        }
    }

    private WsExchangeIdentifierResponse convertBsnToPseudo(final String bsn, final String oin)
            throws IOException, InvalidCipherTextException {

        final Identifier identifier = new Identifier();
        identifier.setBsn(bsn);
        final String oinNencyptedIdentifier = aesGcmSivCryptographer.encrypt(identifier, oin);
        final WsExchangeIdentifierResponse wsExchangeTokenForIdentifier200Response = new WsExchangeIdentifierResponse();
        final WsIdentifier wsIdentifierResponse = new WsIdentifier();
        wsIdentifierResponse.setType(ORGANISATION_PSEUDO);
        wsIdentifierResponse.setValue(oinNencyptedIdentifier);
        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifierResponse);
        return wsExchangeTokenForIdentifier200Response;
    }

    private WsExchangeIdentifierResponse convertPseudoToBEsn(final String pseudo, final String oin)
            throws IOException, InvalidCipherTextException {

        final Identifier identifier = aesGcmSivCryptographer.decrypt(pseudo, oin);
        final WsExchangeIdentifierResponse wsExchangeTokenForIdentifier200Response = new WsExchangeIdentifierResponse();
        final WsIdentifier wsIdentifierResponse = new WsIdentifier();
        wsIdentifierResponse.setType(BSN);
        wsIdentifierResponse.setValue(identifier.getBsn());
        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifierResponse);
        return wsExchangeTokenForIdentifier200Response;
    }
}
