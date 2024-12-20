package nl.ictu.utils;

import java.security.MessageDigest;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public final class MessageDigestUtil {

    public static final String SHA_256 = "SHA-256";

    @SneakyThrows
    public MessageDigest getMessageDigestSha256() {

        return MessageDigest.getInstance(SHA_256);
    }
}
