package nl.ictu.controller.v1;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.pseudoniemenservice.generated.server.api.GetTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.ictu.service.v1.GetTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public final class GetTokenController implements GetTokenApi, VersionOneController {


    private final GetTokenService getTokenService;

    /**
     * Retrieves a token based on the provided caller identifier and request details.
     *
     * @param callerOIN         The identifier of the caller organization initiating the request.
     * @param wsGetTokenRequest The request object containing the recipient organization identifier and additional details.
     * @return A ResponseEntity containing the token if the request is successful, or a ResponseEntity with a status of UNPROCESSABLE_ENTITY if the
     * token cannot be retrieved.
     */
    @Override
    @SneakyThrows
    public ResponseEntity<WsGetTokenResponse> getToken(final String callerOIN,
            final WsGetTokenRequest wsGetTokenRequest) {

        try {
            final var recipientOIN = wsGetTokenRequest.getRecipientOIN();
            final var identifier = wsGetTokenRequest.getIdentifier();
            return ResponseEntity.ok(getTokenService.getWsGetTokenResponse(recipientOIN, identifier));
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
    }
}
