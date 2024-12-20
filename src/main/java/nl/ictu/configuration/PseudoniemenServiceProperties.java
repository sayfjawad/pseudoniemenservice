package nl.ictu.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.experimental.Accessors;
import nl.ictu.service.exception.TokenPrivateKeyException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "pseudoniemenservice")
@Data
@Accessors(chain = true)
public final class PseudoniemenServiceProperties {

    private String tokenPrivateKey;

    private String identifierPrivateKey;

    @PostConstruct
    public void validate() {

        if (!StringUtils.hasText(tokenPrivateKey)) {
            throw new TokenPrivateKeyException("Please set a private token key");
        }
    }
}

