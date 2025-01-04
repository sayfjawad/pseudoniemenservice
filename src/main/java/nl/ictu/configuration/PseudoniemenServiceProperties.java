package nl.ictu.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Een @Configuration is een beter amnnotatie aanduiding voor deze class
 */
@Component
@ConfigurationProperties(prefix = "pseudoniemenservice")
@Getter
@Setter
@Accessors(chain = true)
public class PseudoniemenServiceProperties {

    private String tokenPrivateKey;
    private String identifierPrivateKey;
}

