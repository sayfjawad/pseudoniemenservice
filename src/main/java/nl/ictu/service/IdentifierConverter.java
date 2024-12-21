package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import nl.ictu.Identifier;

public interface IdentifierConverter {

    String encode(Identifier identifier) throws IOException;

    Identifier decode(String encodedIdentifier) throws JsonProcessingException;
}
