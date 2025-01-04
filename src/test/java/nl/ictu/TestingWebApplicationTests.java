package nl.ictu;

import static java.util.Map.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;

/**
 * 3 test classes voor het gehele applicatie? Dat lijkt me stug!
 *
 * is de coveage goed? Echt goed of nep goed?
 */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestingWebApplicationTests {

    @Autowired
    private Environment environment;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {

    }

    @Test
    public void testActuatorHealthEndpoint() {

        final int actuatorPort = environment.getProperty("local.management.port", Integer.class);
        assertThat(
                restTemplate
                        .getForObject("http://localhost:" + actuatorPort + "/actuator/health",
                                String.class)
        ).contains("{\"status\":\"UP\"}");
    }

    @Test
    public void testGetAtokenExchangeForBSN() {
        // get a token
        final Map getTokenBody = Map.of("recipientOIN", "54321543215432154321", "identifier",
                Map.of("type", "BSN", "value", "012345679"));
        final HttpEntity httpEntityGetToken = new HttpEntity(getTokenBody, new HttpHeaders(
                CollectionUtils.toMultiValueMap(
                        of("callerOIN", List.of("0912345012345012345012345")))));
        final ResponseEntity<Map> tokenExchange = restTemplate.exchange("/v1/getToken",
                HttpMethod.POST, httpEntityGetToken, Map.class);
        assertThat(tokenExchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tokenExchange)
                .extracting("body")
                .asInstanceOf(InstanceOfAssertFactories.map(String.class, Void.class))
                .containsKey("token");
        // change token for identifier
        final String token = (String) tokenExchange.getBody().get("token");
        final Map exchangeTokenBody = Map.of("token", token, "identifierType", "BSN");
        final HttpEntity httpEntityExchangeToken = new HttpEntity(exchangeTokenBody,
                new HttpHeaders(CollectionUtils.toMultiValueMap(
                        of("callerOIN", List.of("54321543215432154321")))));
        final ResponseEntity<Map> identifierExchange = restTemplate.exchange("/v1/exchangeToken",
                HttpMethod.POST, httpEntityExchangeToken, Map.class);
        assertThat(identifierExchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(identifierExchange)
                .extracting("body")
                .asInstanceOf(InstanceOfAssertFactories.map(String.class, Map.class))
                .containsExactly(entry("identifier", Map.of("type", "BSN", "value", "012345679")));
    }
}