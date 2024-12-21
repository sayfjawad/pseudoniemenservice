package nl.ictu.controller.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
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
public final class GetTokenController implements GetTokenApi, VersionOneController {

    public static final String V_1 = "v1";
    private final AesGcmCryptographer aesGcmCryptographer;
    private final AesGcmSivCryptographer aesGcmSivCryptographer;
    private final TokenConverter tokenConverter;


    @Override
    @SneakyThrows
    public ResponseEntity<WsGetTokenResponse> getToken(final String callerOIN,
            final WsGetTokenRequest wsGetTokenRequest) {

        // check is callerOIN allowed to communicatie with sinkOIN

        final var creationDate = System.currentTimeMillis();
        final var recipientOIN = wsGetTokenRequest.getRecipientOIN();
        final var identifier = wsGetTokenRequest.getIdentifier();

        var bsn = "";
        if (identifier != null
                && identifier.getValue() != null
                && (BSN.equals(identifier.getType()) || ORGANISATION_PSEUDO.equals(identifier.getType()))) {
            final String bsnValue = identifier.getValue();
            if (BSN.equals(identifier.getType())) {
                bsn = bsnValue;
            } else if (ORGANISATION_PSEUDO.equals(identifier.getType())) {
                bsn = mapDecryptedBsn(bsnValue, recipientOIN);
            }
            final var token = Token.builder()
                    .version(V_1)
                    .bsn(bsn)
                    .creationDate(creationDate)
                    .recipientOIN(recipientOIN)
                    .build();
            final var plainTextToken = tokenConverter.encode(token);
            final var encryptedToken = aesGcmCryptographer.encrypt(plainTextToken, recipientOIN);
            final WsGetTokenResponse wsGetToken200Response = new WsGetTokenResponse();
            wsGetToken200Response.token(encryptedToken);
            return ResponseEntity.ok(wsGetToken200Response);
        }

        return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();

    }

    private String mapDecryptedBsn(final String bsnValue, final String recipientOIN)
            throws InvalidCipherTextException, JsonProcessingException {

        final var decodedIdentifier = aesGcmSivCryptographer.decrypt(
                bsnValue,
                recipientOIN);
        return decodedIdentifier.getBsn();
    }
}
