package nl.ictu;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.of;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestingWebApplicationTests {

    @LocalServerPort
    private int port;

    @Value("${management.server.port}")
    private int actuatorPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testActuatorHealthEndpoint() {
        assertThat(
            restTemplate
                .getForObject("http://localhost:" + actuatorPort + "/actuator/health", String.class)
        ).contains("{\"status\":\"UP\"}");
    }

    @Test
    public void testIntegration() {

        final Map body = Map.of("recipientOIN", "54321543215432154321", "identifier", Map.of("type", "BSN", "value", "012345679"));

        final HttpEntity httpEntity = new HttpEntity(body, new HttpHeaders(CollectionUtils.toMultiValueMap(of("callerOIN", List.of("0912345012345012345012345")))));

        ResponseEntity<Map> exchange = restTemplate.exchange("http://localhost:" + port + "/v1/getToken", HttpMethod.POST, httpEntity, Map.class);

        log.info("map: " + exchange.getBody());

        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}