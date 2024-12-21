package nl.ictu.controller.v1;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.Token;
import nl.ictu.pseudoniemenservice.generated.server.api.GetTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.ictu.service.AesGcmCryptographer;
import nl.ictu.service.AesGcmSivCryptographer;
import nl.ictu.service.TokenConverter;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public final class GetToken implements GetTokenApi, VersionOneController {

    private final AesGcmCryptographer aesGcmCryptographer;
    private final AesGcmSivCryptographer aesGcmSivCryptographer;
    private final TokenConverter tokenConverter;


    @Override
    @SneakyThrows
    public ResponseEntity<WsGetTokenResponse> getToken(final String callerOIN,
            final WsGetTokenRequest wsGetTokenRequest) {

        // check is callerOIN allowed to communicatie with sinkOIN

        final WsGetTokenResponse wsGetToken200Response = new WsGetTokenResponse();

        final var creationDate = System.currentTimeMillis();
        final var recipientOIN = wsGetTokenRequest.getRecipientOIN();
        final var identifier = wsGetTokenRequest.getIdentifier();
        final var tokenBuilder = Token.builder();

        if (identifier != null) {
            switch (identifier.getType()) {
                case BSN -> tokenBuilder
                        .bsn(identifier.getValue())
                        .creationDate(creationDate)
                        .recipientOIN(recipientOIN);
                case ORGANISATION_PSEUDO -> tokenBuilder
                        .bsn(mapDecryptedBsn(identifier.getValue(), recipientOIN))
                        .creationDate(creationDate)
                        .recipientOIN(recipientOIN);
                default -> {
                    return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
                }
            }
        }

        final var plainTextToken = tokenConverter.encode(tokenBuilder.build());
        final var encryptedToken = aesGcmCryptographer.encrypt(plainTextToken, recipientOIN);
        wsGetToken200Response.token(encryptedToken);
        return ResponseEntity.ok(wsGetToken200Response);
    }

    private String mapDecryptedBsn(final String bsnValue, final String recipientOIN)
            throws InvalidCipherTextException, JsonProcessingException {

        final var orgPseudoEncryptedString = bsnValue;
        final var decodedIdentifier = aesGcmSivCryptographer.decrypt(
                orgPseudoEncryptedString,
                recipientOIN);
        return decodedIdentifier.getBsn();
    }
}
