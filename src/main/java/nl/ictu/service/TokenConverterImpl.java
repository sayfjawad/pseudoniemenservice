package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import lombok.RequiredArgsConstructor;
import nl.ictu.Token;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Service;

@SuppressWarnings("DesignForExtension")
@Service
@RequiredArgsConstructor
@RegisterReflectionForBinding({Token.class})
public class TokenConverterImpl implements TokenConverter {

    private final ObjectMapper objectMapper;

    @Override
    public String encode(final Token token) throws IOException {

        final StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, token);
        return stringWriter.toString();
    }

    @Override
    public Token decode(final String encodedToken) throws JsonProcessingException {

        return objectMapper.readValue(encodedToken, Token.class);
    }
}
