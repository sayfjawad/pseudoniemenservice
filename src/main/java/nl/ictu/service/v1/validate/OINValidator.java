package nl.ictu.service.v1.validate;

import lombok.RequiredArgsConstructor;
import nl.ictu.Token;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OINValidator {

    /**
     * Determines if the caller's OIN matches the recipient OIN from the token.
     *
     * @param callerOIN the OIN of the caller
     * @param token the Token object containing recipient OIN
     * @return true if the caller's OIN matches the recipient OIN, false otherwise
     */
    public boolean isValid(final String callerOIN, final Token token) {

        return callerOIN.equals(token.getRecipientOIN());
    }
}
