package nl.ictu.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.Token;
import nl.ictu.pseudoniemenservice.generated.server.api.GetTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetToken200Response;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.ictu.service.Cryptographer;
import nl.ictu.service.TokenConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public final class GetToken implements GetTokenApi, VersionOneController {

    private final Cryptographer cryptographer;

    private final TokenConverter tokenConverter;

    @SneakyThrows
    @Override
    public ResponseEntity<WsGetToken200Response> getToken(final String callerOIN, final WsGetTokenRequest wsGetTokenRequest) {

        // check is callerOIN allowed to communicatie with sinkOIN

        final WsGetToken200Response wsGetToken200Response = new WsGetToken200Response();

        final Token token = new Token();

        token.setCreationDate(LocalDateTime.now());
        token.setRecipientOIN(wsGetTokenRequest.getRecipientOIN());

        if (wsGetTokenRequest.getIdentifier() != null) {
            if (WsIdentifierTypes.BSN.equals(wsGetTokenRequest.getIdentifier().getType())) {
                token.setBsn(wsGetTokenRequest.getIdentifier().getValue());
            }
        }

        final String plainTextToken = tokenConverter.encode(token);

        wsGetToken200Response.token(cryptographer.encrypt(plainTextToken));

        return ResponseEntity.ok(wsGetToken200Response);
    }

}
