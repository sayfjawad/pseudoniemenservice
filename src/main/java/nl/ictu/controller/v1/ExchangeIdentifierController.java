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
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public final class ExchangeIdentifierController implements ExchangeIdentifierApi, VersionOneController {

    public static final String V_1 = "v1";
    private final AesGcmSivCryptographer aesGcmSivCryptographer;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeIdentifierResponse> exchangeIdentifier(final String callerOIN,
            final WsExchangeIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {

        final var wsIdentifierRequest = wsExchangeIdentifierForIdentifierRequest.getIdentifier();
        final var recipientOIN = wsExchangeIdentifierForIdentifierRequest.getRecipientOIN();
        final var recipientIdentifierType = wsExchangeIdentifierForIdentifierRequest.getRecipientIdentifierType();
        if (BSN.equals(wsIdentifierRequest.getType()) && ORGANISATION_PSEUDO.equals(
                recipientIdentifierType)) {
            // from BSN to Org Pseudo
            return ResponseEntity.ok(
                    mapBsnToPseudo(wsIdentifierRequest.getValue(), recipientOIN));
        } else if (ORGANISATION_PSEUDO.equals(wsIdentifierRequest.getType()) && BSN.equals(
                recipientIdentifierType)) {
            // from BSN to Org Pseudo
            return ResponseEntity.ok(
                    mapPseudoToBEsn(wsIdentifierRequest.getValue(), recipientOIN));
        }
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
    }

    private WsExchangeIdentifierResponse mapBsnToPseudo(final String bsn, final String oin)
            throws IOException, InvalidCipherTextException {

        return WsExchangeIdentifierResponse.builder()
                .identifier(WsIdentifier.builder()
                        .type(ORGANISATION_PSEUDO)
                        .value(aesGcmSivCryptographer.encrypt(Identifier.builder()
                                .version(V_1)
                                .bsn(bsn)
                                .build(), oin))
                        .build())
                .build();
    }

    private WsExchangeIdentifierResponse mapPseudoToBEsn(final String pseudo, final String oin)
            throws IOException, InvalidCipherTextException {

        return WsExchangeIdentifierResponse.builder()
                .identifier(WsIdentifier.builder()
                        .type(BSN)
                        .value(aesGcmSivCryptographer.decrypt(pseudo, oin).getBsn())
                        .build())
                .build();
    }
}
