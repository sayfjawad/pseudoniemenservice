package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import nl.ictu.Token;

public interface TokenConverter {

    String encode(Token token) throws IOException;

    Token decode(String encodedToken) throws JsonProcessingException;
}
