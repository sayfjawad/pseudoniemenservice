package nl.ictu.service.v1.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import lombok.RequiredArgsConstructor;
import nl.ictu.model.Identifier;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Service;

@SuppressWarnings("DesignForExtension")
@Service
@RequiredArgsConstructor
@RegisterReflectionForBinding({Identifier.class})
public class IdentifierConverter {

    private final ObjectMapper objectMapper;

    public String encode(final Identifier identifier) throws IOException {

        final StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, identifier);
        return stringWriter.toString();
    }

    public Identifier decode(final String encodedIdentifier) throws JsonProcessingException {

        return objectMapper.readValue(encodedIdentifier, Identifier.class);
    }
}
