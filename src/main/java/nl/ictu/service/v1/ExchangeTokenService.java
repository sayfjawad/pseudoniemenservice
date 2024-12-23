package nl.ictu.service.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.Identifier;
import nl.ictu.controller.exception.InvalidOINException;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.service.exception.InvalidWsIdentifierTokenException;
import nl.ictu.service.v1.crypto.AesGcmCryptographer;
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import nl.ictu.service.v1.crypto.TokenConverter;
import nl.ictu.service.v1.validate.OINValidator;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeTokenService {

    public static final String V_1 = "v1";
    private final AesGcmCryptographer aesGcmCryptographer;
    private final AesGcmSivCryptographer aesGcmSivCryptographer;
    private final TokenConverter tokenConverter;
    private final OINValidator oinValidator;

    @SneakyThrows
    public WsExchangeTokenResponse exchangeToken(final String callerOIN,
            final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest) {

        final var encodedToken = aesGcmCryptographer.decrypt(wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);
        final var token = tokenConverter.decode(encodedToken);
        if (!oinValidator.isValid(callerOIN, token)) {
            throw new InvalidOINException("Sink OIN not the same");
        }

        final var wsIdentifier = new WsIdentifier();
        switch (wsExchangeTokenForIdentifierRequest.getIdentifierType()) {
            case BSN -> {
                wsIdentifier.setType(BSN);
                wsIdentifier.setValue(token.getBsn());
            }
            case ORGANISATION_PSEUDO -> {
                final String encrypt = aesGcmSivCryptographer.encrypt(Identifier.builder()
                        .version(V_1)
                        .bsn(token.getBsn())
                        .build(), callerOIN);
                wsIdentifier.setType(ORGANISATION_PSEUDO);
                wsIdentifier.setValue(encrypt);
            }
            default -> throw new InvalidWsIdentifierTokenException("Invalid identifier cannot be processed.");
        }
        return WsExchangeTokenResponse.builder()
                .identifier(wsIdentifier)
                .build();
    }
}
