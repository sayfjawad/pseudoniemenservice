package nl.ictu.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.psuedoniemenservice.generated.server.api.GetTokenApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsGetToken200Response;
import nl.ictu.psuedoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.ictu.service.Cryptographer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetToken implements GetTokenApi, VersionOneController {

    private final Cryptographer cryptographer;

    @SneakyThrows
    @Override
    public ResponseEntity<WsGetToken200Response> getToken(final WsGetTokenRequest wsGetTokenRequest) {
        final WsGetToken200Response wsGetToken200Response = new WsGetToken200Response();

        final String plainTextToken = TokenHelper.encode(wsGetTokenRequest);

        wsGetToken200Response.token(cryptographer.encrypt(plainTextToken));

        return ResponseEntity.ok(wsGetToken200Response);
    }

}
