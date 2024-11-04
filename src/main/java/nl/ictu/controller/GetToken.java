package nl.ictu.controller;

import nl.ictu.psuedoniemenservice.generated.server.api.GetTokenApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsGetTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GetToken implements GetTokenApi {
    @Override
    public ResponseEntity<String> getToken(final WsGetTokenRequest wsGetTokenRequest) {
        return ResponseEntity.ok(UUID.randomUUID().toString());
    }
}
