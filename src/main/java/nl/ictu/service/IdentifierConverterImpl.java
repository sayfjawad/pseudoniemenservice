package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nl.ictu.Identifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;

@SuppressWarnings("DesignForExtension")
@Service
@RequiredArgsConstructor
public class IdentifierConverterImpl implements IdentifierConverter {

    private final ObjectMapper objectMapper;

    @Override
    public String encode(final Identifier identifier) throws IOException {
        final StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, identifier);
        return stringWriter.toString();
    }

    @Override
    public Identifier decode(final String encodedIdentifier) throws JsonProcessingException {
        return objectMapper.readValue(encodedIdentifier, Identifier.class);
    }


}
