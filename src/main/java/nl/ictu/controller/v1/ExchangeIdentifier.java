package nl.ictu.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.Identifier;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierForIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenForIdentifier200Response;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.ictu.service.AesGcmSivCryptographer;
import nl.ictu.service.IdentifierConverter;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@RequiredArgsConstructor
@RestController
public final class ExchangeIdentifier implements ExchangeIdentifierApi, VersionOneController {

    private final IdentifierConverter identifierConverter;

    private final AesGcmSivCryptographer aesGcmSivCryptographer;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeIdentifierForIdentifier(final String callerOIN, final WsExchangeIdentifierForIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {

        final WsIdentifier wsIdentifierRequest = wsExchangeIdentifierForIdentifierRequest.getIdentifier();

        final String recipientOIN = wsExchangeIdentifierForIdentifierRequest.getRecipientOIN();

        final WsIdentifierTypes recipientIdentifierType = wsExchangeIdentifierForIdentifierRequest.getRecipientIdentifierType();

        if (BSN.equals(wsIdentifierRequest.getType()) && ORGANISATION_PSEUDO.equals(recipientIdentifierType)) {
            // from BSN to Org Pseudo
            return ResponseEntity.ok(convertBsnToPseudo(wsIdentifierRequest.getValue(), recipientOIN));

        } else if (ORGANISATION_PSEUDO.equals(wsIdentifierRequest.getType()) && BSN.equals(recipientIdentifierType)) {
            // from BSN to Org Pseudo
            return ResponseEntity.ok(convertPseudoToBEsn(wsIdentifierRequest.getValue(), recipientOIN));

        } else {
            return ResponseEntity.status(NOT_IMPLEMENTED).build();
        }


    }

    private WsExchangeTokenForIdentifier200Response convertBsnToPseudo(final String bsn, final String oin) throws IOException, InvalidCipherTextException {

        final Identifier identifier = new Identifier();

        identifier.setBsn(bsn);

        final String encode = identifierConverter.encode(identifier);

        final String oinNencyptedIdentifier = aesGcmSivCryptographer.encrypt(encode, oin);

        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        final WsIdentifier wsIdentifierResponse = new WsIdentifier();

        wsIdentifierResponse.setType(ORGANISATION_PSEUDO);
        wsIdentifierResponse.setValue(oinNencyptedIdentifier);

        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifierResponse);

        return wsExchangeTokenForIdentifier200Response;

    }

    private WsExchangeTokenForIdentifier200Response convertPseudoToBEsn(final String pseudo, final String oin) throws IOException, InvalidCipherTextException {

        final String encodedIdentifier = aesGcmSivCryptographer.decrypt(pseudo, oin);

        final Identifier identifier = identifierConverter.decode(encodedIdentifier);

        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        final WsIdentifier wsIdentifierResponse = new WsIdentifier();

        wsIdentifierResponse.setType(BSN);
        wsIdentifierResponse.setValue(identifier.getBsn());

        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifierResponse);

        return wsExchangeTokenForIdentifier200Response;

    }
}
