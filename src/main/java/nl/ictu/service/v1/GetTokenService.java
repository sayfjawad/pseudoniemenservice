package nl.ictu.service.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.Token;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.service.v1.crypto.AesGcmCryptographer;
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import nl.ictu.service.v1.crypto.TokenConverter;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class GetTokenService {

    public static final String V_1 = "v1";
    private final AesGcmCryptographer aesGcmCryptographer;
    private final AesGcmSivCryptographer aesGcmSivCryptographer;
    private final TokenConverter tokenConverter;

    @SneakyThrows
    public WsGetTokenResponse getWsGetTokenResponse(final String recipientOIN, final WsIdentifier identifier) {

        final var creationDate = System.currentTimeMillis();

        // check is callerOIN allowed to communicatie with sinkOIN
        if (identifier != null) {

            final String bsn = mapBsn(identifier, recipientOIN);
            if (bsn != null) {
                return createEncryptedToken(bsn, creationDate, recipientOIN);
            }
        }
        return null;
    }

    @SneakyThrows
    private String mapBsn(final WsIdentifier identifier, final String recipientOIN) {

        final String bsnValue = identifier.getValue();
        if (BSN.equals(identifier.getType())) {
            return bsnValue;
        } else if (ORGANISATION_PSEUDO.equals(identifier.getType())) {
            return mapDecryptedBsn(bsnValue, recipientOIN);
        }
        return null;
    }

    private WsGetTokenResponse createEncryptedToken(final String bsn, final long creationDate,
            final String recipientOIN)
            throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

        final var plainTextToken = tokenConverter.encode(Token.builder()
                .version(V_1)
                .bsn(bsn)
                .creationDate(creationDate)
                .recipientOIN(recipientOIN)
                .build());
        final var encryptedToken = aesGcmCryptographer.encrypt(plainTextToken, recipientOIN);
        final var wsGetToken200Response = new WsGetTokenResponse();
        wsGetToken200Response.token(encryptedToken);
        return wsGetToken200Response;
    }

    private String mapDecryptedBsn(final String bsnValue, final String recipientOIN)
            throws InvalidCipherTextException, JsonProcessingException {

        final var decodedIdentifier = aesGcmSivCryptographer.decrypt(bsnValue, recipientOIN);
        return decodedIdentifier.getBsn();
    }
}
