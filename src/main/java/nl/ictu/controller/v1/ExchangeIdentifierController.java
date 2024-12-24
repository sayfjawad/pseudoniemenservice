package nl.ictu.controller.v1;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.service.v1.ExchangeIdentifierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public final class ExchangeIdentifierController implements ExchangeIdentifierApi,
        VersionOneController {

    private final ExchangeIdentifierService service;

    /**
     * Exchanges an identifier based on the provided caller OIN and request data.
     *
     * @param callerOIN         The OIN of the caller initiating the request.
     * @param wsExchangeRequest The request object containing the identifier and additional data for the exchange process.
     * @return A ResponseEntity containing a WsExchangeIdentifierResponse if the exchange is successful, or a ResponseEntity with HTTP status
     * UNPROCESSABLE_ENTITY if the exchange fails.
     */
    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeIdentifierResponse> exchangeIdentifier(final String callerOIN,
            final WsExchangeIdentifierRequest wsExchangeRequest) {

        try {
            final var identifier = service.exchangeIdentifier(callerOIN, wsExchangeRequest);
            return ResponseEntity.ok(identifier);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
        }
    }
}
