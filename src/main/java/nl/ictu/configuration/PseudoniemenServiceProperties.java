package nl.ictu.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pseudoniemenservice")
@Getter
@Setter
public class PseudoniemenServiceProperties {

    private String tokenPrivateKey;

}
