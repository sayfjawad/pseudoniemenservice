package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.ictu.Identifier;

import java.io.IOException;

public interface IdentifierConverter {

    String encode(Identifier identifier) throws IOException;

    Identifier decode(String encodedIdentifier) throws JsonProcessingException;

}
