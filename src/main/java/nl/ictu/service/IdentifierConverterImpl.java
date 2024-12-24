package nl.ictu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import lombok.RequiredArgsConstructor;
import nl.ictu.Identifier;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Service;

@SuppressWarnings("DesignForExtension")
@Service
@RequiredArgsConstructor
@RegisterReflectionForBinding({Identifier.class})
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
