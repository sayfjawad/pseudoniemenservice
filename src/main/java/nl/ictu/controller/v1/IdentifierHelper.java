package nl.ictu.controller.v1;

import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.psuedoniemenservice.generated.server.model.WsIdentifierTypes;

public final class IdentifierHelper {

    private final static String DELIMITER = ":";

    public static String encode(final WsIdentifier wsIdentifier) {
        return wsIdentifier.getIdentifierType().name() + DELIMITER + wsIdentifier.getIdentifierValue();
    }

    public static WsIdentifier decode(final String encoded) {

        final String[] parts = encoded.split(DELIMITER);

        final WsIdentifier wsIdentifier = new WsIdentifier();

        wsIdentifier.identifierType(WsIdentifierTypes.fromValue(parts[0]));

        wsIdentifier.identifierValue(parts[1]);

        return wsIdentifier;

    }

}
