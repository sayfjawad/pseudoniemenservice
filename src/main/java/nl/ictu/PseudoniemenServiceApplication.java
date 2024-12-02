package nl.ictu;


import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.NoSuchAlgorithmException;

@SuppressWarnings("HideUtilityClassConstructor")
@SpringBootApplication
@NoArgsConstructor
public class PseudoniemenServiceApplication {

    public static void main(final String[] args) throws NoSuchAlgorithmException {
        SpringApplication.run(PseudoniemenServiceApplication.class, args);
    }
}
