package nl.ictu.service.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.model.Token;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.service.exception.WsGetTokenProcessingException;
import nl.ictu.service.v1.crypto.AesGcmCryptographer;
import nl.ictu.service.v1.crypto.TokenCoder;
import nl.ictu.service.v1.map.EncryptedBsnMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public final class GetTokenService {

    public static final String V_1 = "v1";
    private final AesGcmCryptographer aesGcmCryptographer;
    private final TokenCoder tokenCoder;
    private final EncryptedBsnMapper encryptedBsnMapper;

    /**
     * Generates an encrypted token response based on the given recipient OIN and identifier. Validates the identifier type and maps it to the
     * corresponding BSN before creating the encrypted token.
     *
     * @param recipientOIN the recipient's organizational identification number
     * @param identifier   the identifier containing value and type information
     * @return a {@link WsGetTokenResponse} containing the encrypted token, or null if the identifier is invalid or BSN mapping fails
     */
    @SneakyThrows
    public WsGetTokenResponse getWsGetTokenResponse(final String recipientOIN, final WsIdentifier identifier) {

        final var creationDate = System.currentTimeMillis();

        // check is callerOIN allowed to communicatie with sinkOIN
        try {
            final String bsn = mapBsn(identifier, recipientOIN);
            return createEncryptedToken(bsn, creationDate, recipientOIN);
        } catch (Exception ex) {
            final var exceptionMessage = ex.getMessage();
            log.warn(exceptionMessage);
            throw new WsGetTokenProcessingException(exceptionMessage);
        }
    }

    private String mapBsn(final WsIdentifier identifier, final String recipientOIN) {

        final String bsnValue = identifier.getValue();
        if (BSN.equals(identifier.getType())) {
            return bsnValue;
        } else if (ORGANISATION_PSEUDO.equals(identifier.getType())) {
            return encryptedBsnMapper.map(bsnValue, recipientOIN);
        }
        return null;
    }

    private WsGetTokenResponse createEncryptedToken(final String bsn, final long creationDate,
            final String recipientOIN)
            throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

        final var plainTextToken = tokenCoder.encode(Token.builder()
                .version(V_1)
                .bsn(bsn)
                .creationDate(creationDate)
                .recipientOIN(recipientOIN)
                .build());
        final var encryptedToken = aesGcmCryptographer.encrypt(plainTextToken, recipientOIN);
        return WsGetTokenResponse.builder()
                .token(encryptedToken)
                .build();
    }
}
