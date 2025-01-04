package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import nl.ictu.Identifier;
/**
 * Wat is de toegevoegde waarde van een interface als het maar een keer geimplementeerd wordt?
 */
public interface IdentifierConverter {

    String encode(Identifier identifier) throws IOException;

    Identifier decode(String encodedIdentifier) throws JsonProcessingException;
}
