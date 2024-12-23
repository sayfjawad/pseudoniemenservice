package nl.ictu.service.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.Identifier;
import nl.ictu.Token;
import nl.ictu.controller.exception.InvalidOINException;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.service.exception.InvalidWsIdentifierTokenException;
import nl.ictu.service.v1.crypto.AesGcmCryptographer;
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import nl.ictu.service.v1.crypto.TokenCoder;
import nl.ictu.service.v1.validate.OINValidator;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeTokenService {

    public static final String V_1 = "v1";
    private final AesGcmCryptographer aesGcmCryptographer;
    private final AesGcmSivCryptographer aesGcmSivCryptographer;
    private final TokenCoder tokenCoder;
    private final OINValidator oinValidator;

    /**
     * Exchanges a token for an identifier based on the provided request and caller OIN.
     *
     * @param callerOIN                           the originating organization's identification
     *                                            number used for validation
     * @param wsExchangeTokenForIdentifierRequest the request containing the token and identifier
     *                                            type
     * @return a WsExchangeTokenResponse containing the generated or resolved identifier
     * @throws InvalidOINException               if the caller OIN is not valid or does not match
     *                                           the token
     * @throws InvalidWsIdentifierTokenException if the identifier type in the request is invalid or
     *                                           cannot be processed
     */
    @SneakyThrows
    public WsExchangeTokenResponse exchangeToken(final String callerOIN,
            final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest) {

        final var encodedToken = aesGcmCryptographer.decrypt(wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);
        final var token = tokenCoder.decode(encodedToken);
        if (!oinValidator.isValid(callerOIN, token)) {
            throw new InvalidOINException("Sink OIN not the same");
        }
        switch (wsExchangeTokenForIdentifierRequest.getIdentifierType()) {
            case BSN -> {
                return mapBsnToken(token);
            }
            case ORGANISATION_PSEUDO -> {
                return mapOrganisationPseudoToken(callerOIN, token);
            }
            default -> throw new InvalidWsIdentifierTokenException(
                    "Invalid identifier cannot be processed.");
        }
    }

    private static WsExchangeTokenResponse mapBsnToken(final Token token) {

        return WsExchangeTokenResponse.builder()
                .identifier(WsIdentifier.builder()
                        .type(BSN)
                        .value(token.getBsn())
                        .build())
                .build();
    }

    private WsExchangeTokenResponse mapOrganisationPseudoToken(final String callerOIN,
            final Token token) throws InvalidCipherTextException, IOException {

        final var tokenIdentifier = Identifier.builder()
                .version(V_1)
                .bsn(token.getBsn())
                .build();
        final var encrypt = aesGcmSivCryptographer.encrypt(tokenIdentifier, callerOIN);
        return WsExchangeTokenResponse.builder()
                .identifier(WsIdentifier.builder()
                        .type(ORGANISATION_PSEUDO)
                        .value(encrypt)
                        .build())
                .build();
    }
}
