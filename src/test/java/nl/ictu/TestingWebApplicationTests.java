package nl.ictu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void testActuatorHealthEndpoint() throws Exception {
        assertThat(
            restTemplate
                .getForObject("http://localhost:" + actuatorPort + "/actuator/health", String.class)
        ).contains("{\"status\":\"UP\"}");
    }

}