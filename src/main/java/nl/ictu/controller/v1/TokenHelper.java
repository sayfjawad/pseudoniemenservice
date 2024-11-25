package nl.ictu.controller.v1;

import nl.ictu.psuedoniemenservice.generated.server.model.WsGetTokenRequest;

import java.util.StringJoiner;

public final class TokenHelper {

    private static final String DELIMITER = "_";

    public static String encode(final WsGetTokenRequest wsGetTokenRequest) {

        final StringJoiner joiner = new StringJoiner(DELIMITER);

        joiner.add(wsGetTokenRequest.getReceiverOin());
        joiner.add(wsGetTokenRequest.getIdentifier().getIdentifierType() + wsGetTokenRequest.getIdentifier().getIdentifierValue());

        final String encodedToken = wsGetTokenRequest.getRequesterOin() + DELIMITER + IdentifierHelper.encode(wsGetTokenRequest.getIdentifier()) + DELIMITER + wsGetTokenRequest.getReceiverOin();

        return encodedToken;

    }

    public static WsGetTokenRequest decode(final String encodedToken) {

        final String[] parts = encodedToken.split(DELIMITER);

        final WsGetTokenRequest wsGetTokenRequest = new WsGetTokenRequest();

        wsGetTokenRequest.setRequesterOin(parts[0]);
        wsGetTokenRequest.setIdentifier(IdentifierHelper.decode(parts[1]));
        wsGetTokenRequest.setReceiverOin(parts[2]);

        return wsGetTokenRequest;

    }

}
