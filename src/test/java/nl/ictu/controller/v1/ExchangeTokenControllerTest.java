package nl.ictu.controller.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.ictu.controller.exception.InvalidOINException;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.ictu.service.v1.ExchangeTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExchangeTokenController.class)
class ExchangeTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExchangeTokenService exchangeTokenService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("exchangeToken() -> 200 OK on success")
    void exchangeToken_ShouldReturnOk() throws Exception {
        // GIVEN: a request payload
        WsExchangeTokenRequest requestPayload = new WsExchangeTokenRequest();
        requestPayload.setToken("testToken");
        requestPayload.setIdentifierType(WsIdentifierTypes.BSN);
        // AND: a mock service response
        WsExchangeTokenResponse responsePayload = new WsExchangeTokenResponse();
        responsePayload.setIdentifier(WsIdentifier.builder()
                .type(WsIdentifierTypes.BSN)
                .value("convertedIdentifier")
                .build());
        // WHEN: the service is called, return the response payload
        when(exchangeTokenService.exchangeToken(eq("TEST_OIN"), any(WsExchangeTokenRequest.class)))
                .thenReturn(responsePayload);
        // THEN: perform the POST request
        mockMvc.perform(
                        post("/v1/exchangeToken")
                                .header("callerOIN", "TEST_OIN")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestPayload))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.identifier.value").value("convertedIdentifier"))
                .andExpect(jsonPath("$.identifier.type").value("BSN"));
    }

    @Test
    @DisplayName("exchangeToken() -> 422 UNPROCESSABLE_ENTITY on exception")
    void exchangeToken_ShouldReturnUnprocessableEntity() throws Exception {
        // GIVEN: a request payload
        WsExchangeTokenRequest requestPayload = new WsExchangeTokenRequest();
        requestPayload.setToken("testToken");
        requestPayload.setIdentifierType(WsIdentifierTypes.ORGANISATION_PSEUDO);
        // WHEN: the service throws an exception
        doThrow(new InvalidOINException("Service error"))
                .when(exchangeTokenService)
                .exchangeToken(eq("FAIL_OIN"), any(WsExchangeTokenRequest.class));
        // THEN: perform the POST request
        mockMvc.perform(
                        post("/v1/exchangeToken")
                                .header("callerOIN", "FAIL_OIN")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestPayload))
                )
                .andExpect(status().isUnprocessableEntity());
    }
}
