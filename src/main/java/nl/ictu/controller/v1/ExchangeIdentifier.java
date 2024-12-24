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

@RequiredArgsConstructor
@RestController
public final class ExchangeIdentifier implements ExchangeIdentifierApi, VersionOneController {

    private final AesGcmSivCryptographer aesGcmSivCryptographer;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeIdentifierResponse> exchangeIdentifier(final String callerOIN,
            final WsExchangeIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {

        final WsIdentifier wsIdentifierRequest = wsExchangeIdentifierForIdentifierRequest.getIdentifier();
        final String recipientOIN = wsExchangeIdentifierForIdentifierRequest.getRecipientOIN();
        final WsIdentifierTypes recipientIdentifierType = wsExchangeIdentifierForIdentifierRequest.getRecipientIdentifierType();
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
