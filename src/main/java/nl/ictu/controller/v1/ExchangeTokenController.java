package nl.ictu.controller.v1;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.service.v1.ExchangeTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeTokenController implements ExchangeTokenApi, VersionOneController {

    private final ExchangeTokenService exchangeTokenService;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeTokenResponse> exchangeToken(final String callerOIN,
            final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest) {

        try {
            final var wsExchangeTokenResponse = exchangeTokenService.exchangeToken(callerOIN, wsExchangeTokenForIdentifierRequest);
            return ResponseEntity.ok(wsExchangeTokenResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
        }
    }
}
