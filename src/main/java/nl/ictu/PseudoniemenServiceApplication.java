package nl.ictu;


import lombok.NoArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

@SuppressWarnings("HideUtilityClassConstructor")
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
