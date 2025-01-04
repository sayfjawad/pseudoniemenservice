package nl.ictu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import lombok.NoArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Regels die genegeerd worden kunnen beter uitgezet of opgelost worden
 */
@SuppressWarnings({"HideUtilityClassConstructor"})
@SuppressFBWarnings(value = "EI_EXPOSE_STATIC_REP2",
        justification = "nl.ictu.PseudoniemenServiceApplication$$SpringCGLIB$$0")
@SpringBootApplication
@NoArgsConstructor
public class PseudoniemenServiceApplication {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(final String[] args) throws NoSuchAlgorithmException {

        SpringApplication.run(PseudoniemenServiceApplication.class, args);
    }
}
