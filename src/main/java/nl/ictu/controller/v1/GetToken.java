package nl.ictu.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.Identifier;
import nl.ictu.Token;
import nl.ictu.pseudoniemenservice.generated.server.api.GetTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetToken200Response;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.ictu.service.AesGcmCryptographer;
import nl.ictu.service.AesGcmSivCryptographer;
import nl.ictu.service.IdentifierConverter;
import nl.ictu.service.TokenConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public final class GetToken implements GetTokenApi, VersionOneController {

    private final AesGcmCryptographer aesGcmCryptographer;

    private final AesGcmSivCryptographer aesGcmSivCryptographer;

    private final TokenConverter tokenConverter;

    private final IdentifierConverter identifierConverter;

    @SneakyThrows
    @Override
    public ResponseEntity<WsGetToken200Response> getToken(final String callerOIN, final WsGetTokenRequest wsGetTokenRequest) {

        // check is callerOIN allowed to communicatie with sinkOIN

        final WsGetToken200Response wsGetToken200Response = new WsGetToken200Response();

        final Token token = new Token();

        token.setCreationDate(System.currentTimeMillis());
        token.setRecipientOIN(wsGetTokenRequest.getRecipientOIN());

        if (wsGetTokenRequest.getIdentifier() != null) {
            switch (wsGetTokenRequest.getIdentifier().getType()) {
                case BSN -> token.setBsn(wsGetTokenRequest.getIdentifier().getValue());
                case ORGANISATION_PSEUDO -> {

                    final String orgPseudoEncryptedString = wsGetTokenRequest.getIdentifier().getValue();

                    final String orgPseudoString = aesGcmSivCryptographer.decrypt(orgPseudoEncryptedString, wsGetTokenRequest.getRecipientOIN());

                    final Identifier decodedIdentifier = identifierConverter.decode(orgPseudoString);

                    token.setBsn(decodedIdentifier.getBsn());

                }
            }
        }

        final String plainTextToken = tokenConverter.encode(token);

        wsGetToken200Response.token(aesGcmCryptographer.encrypt(plainTextToken, wsGetTokenRequest.getRecipientOIN()));

        return ResponseEntity.ok(wsGetToken200Response);
    }

}
