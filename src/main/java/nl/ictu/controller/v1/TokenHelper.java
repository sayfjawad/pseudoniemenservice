package nl.ictu.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.ictu.Token;

import java.io.IOException;
import java.io.StringWriter;

public final class TokenHelper {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String encode(final Token token) throws IOException {
        final StringWriter stringWriter = new StringWriter();
        OBJECT_MAPPER.writeValue(stringWriter, token);
        return stringWriter.toString();
    }

    public static Token decode(final String encodedToken) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(encodedToken, Token.class);
    }

}
