package nl.ictu.controller.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeIdentifierApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.service.v1.map.BsnPseudoMapper;
import nl.ictu.service.v1.map.PseudoBsnMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public final class ExchangeIdentifierController implements ExchangeIdentifierApi,
        VersionOneController {

    private final BsnPseudoMapper bsnPseudoMapper;
    private final PseudoBsnMapper pseudoBsnMapper;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeIdentifierResponse> exchangeIdentifier(final String callerOIN,
            final WsExchangeIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {

        final var wsIdentifierRequest = wsExchangeIdentifierForIdentifierRequest.getIdentifier();
        final var recipientOIN = wsExchangeIdentifierForIdentifierRequest.getRecipientOIN();
        final var recipientIdentifierType = wsExchangeIdentifierForIdentifierRequest.getRecipientIdentifierType();
        if (BSN.equals(wsIdentifierRequest.getType()) && ORGANISATION_PSEUDO.equals(
                recipientIdentifierType)) {
            return ResponseEntity.ok(
                    bsnPseudoMapper.map(wsIdentifierRequest.getValue(), recipientOIN));
        } else if (ORGANISATION_PSEUDO.equals(wsIdentifierRequest.getType()) && BSN.equals(
                recipientIdentifierType)) {
            return ResponseEntity.ok(
                    pseudoBsnMapper.map(wsIdentifierRequest.getValue(), recipientOIN));
        }
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
    }


}
