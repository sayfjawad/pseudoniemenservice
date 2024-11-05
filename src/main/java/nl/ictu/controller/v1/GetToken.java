package nl.ictu.controller.v1;

import nl.ictu.psuedoniemenservice.generated.server.api.GetTokenApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsGetToken200Response;
import nl.ictu.psuedoniemenservice.generated.server.model.WsGetTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GetToken implements GetTokenApi, VersionOneController {
    @Override
    public ResponseEntity<WsGetToken200Response> getToken(final WsGetTokenRequest wsGetTokenRequest) {

        final WsGetToken200Response wsGetToken200Response = new WsGetToken200Response();

        wsGetToken200Response.token(UUID.randomUUID().toString());

        return ResponseEntity.ok(wsGetToken200Response);
    }
}
