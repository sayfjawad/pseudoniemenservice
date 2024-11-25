package nl.ictu.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.psuedoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeTokenForIdentifier200Response;
import nl.ictu.psuedoniemenservice.generated.server.model.WsExchangeTokenForIdentifierRequest;
import nl.ictu.psuedoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.ictu.service.Cryptographer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ExchangeToken implements ExchangeTokenApi, VersionOneController {

    private final Cryptographer cryptographer;

    @SneakyThrows
    @Override
    public ResponseEntity<WsExchangeTokenForIdentifier200Response> exchangeTokenForIdentifier(final WsExchangeTokenForIdentifierRequest wsExchangeTokenForIdentifierRequest) {

        final String encodedToken = cryptographer.decrypt(wsExchangeTokenForIdentifierRequest.getToken());

        final WsGetTokenRequest decodedToken = TokenHelper.decode(encodedToken);

        if (!decodedToken.getReceiverOin().equals(wsExchangeTokenForIdentifierRequest.getReceiverOin())) {
            throw new RuntimeException("ReceiverOIN not the same");
        }

        if (!decodedToken.getRequesterOin().equals(wsExchangeTokenForIdentifierRequest.getRequesterOin())) {
            throw new RuntimeException("RequesterOIN not the same");
        }


        final WsExchangeTokenForIdentifier200Response wsExchangeTokenForIdentifier200Response = new WsExchangeTokenForIdentifier200Response();

        wsExchangeTokenForIdentifier200Response.setIdentifier(decodedToken.getIdentifier());

        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);

    }
}
