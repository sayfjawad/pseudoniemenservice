package nl.ictu.controller.v1;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.service.v1.ExchangeIdentifierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public final class ExchangeIdentifierController implements ExchangeIdentifierApi,
        VersionOneController {

    private final ExchangeIdentifierService service;

    /**
     * Exchanges an identifier based on the provided caller OIN and request data.
     *
     * @param callerOIN The OIN of the caller initiating the request.
     * @param wsExchangeIdentifierForIdentifierRequest The request object containing the identifier and additional data for the exchange process.
     * @return A ResponseEntity containing a WsExchangeIdentifierResponse if the exchange is successful,
     *         or a ResponseEntity with HTTP status UNPROCESSABLE_ENTITY if the exchange fails.
     */
    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeIdentifierResponse> exchangeIdentifier(final String callerOIN,
            final WsExchangeIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {

        final var identifier = service.exchangeIdentifier(callerOIN,
                wsExchangeIdentifierForIdentifierRequest);
        if (identifier != null) {
            return ResponseEntity.ok(identifier);
        }
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
    }
}
