package nl.ictu.utils;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import org.springframework.stereotype.Component;

@Component
public final class Base64Wrapper {

    public static final Decoder DECODER = Base64.getDecoder();
    public static final Encoder ENCODER = Base64.getEncoder();

    public byte[] decode(final String toDecode) {

        return DECODER.decode(toDecode);
    }

    public byte[] encode(final byte[] toEncode) {

        return ENCODER.encode(toEncode);
    }

    public String encodeToString(final byte[] toEncode) {

        return ENCODER.encodeToString(toEncode);
    }
}
