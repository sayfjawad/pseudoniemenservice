package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.ictu.Token;

import java.io.IOException;

public interface TokenConverter {
    String encode(Token token) throws IOException;

    Token decode(String encodedToken) throws JsonProcessingException;
}
