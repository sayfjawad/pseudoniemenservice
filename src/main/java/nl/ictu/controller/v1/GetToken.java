package nl.ictu.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.Token;
import nl.ictu.psuedoniemenservice.generated.server.api.GetTokenApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsGetToken200Response;
import nl.ictu.psuedoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.ictu.service.Cryptographer;
import nl.ictu.service.TokenConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class GetToken implements GetTokenApi, VersionOneController {

    private final Cryptographer cryptographer;

    private final TokenConverter tokenConverter;

    @SneakyThrows
    @Override
    public ResponseEntity<WsGetToken200Response> getToken(final String callerOIN, WsGetTokenRequest wsGetTokenRequest) {

        // check is callerOIN allowed to communicatie with sinkOIN

        final WsGetToken200Response wsGetToken200Response = new WsGetToken200Response();

        final Token token = new Token();

        token.setCreationDate(new Date(System.currentTimeMillis()));
        token.setRecipientOIN(wsGetTokenRequest.getRecipientOIN());
        token.getIdentifier().setType(wsGetTokenRequest.getIdentifier().getIdentifierType().name());
        token.getIdentifier().setValue(wsGetTokenRequest.getIdentifier().getIdentifierValue());

        final String plainTextToken = tokenConverter.encode(token);

        wsGetToken200Response.token(cryptographer.encrypt(plainTextToken));

        return ResponseEntity.ok(wsGetToken200Response);
    }

}
