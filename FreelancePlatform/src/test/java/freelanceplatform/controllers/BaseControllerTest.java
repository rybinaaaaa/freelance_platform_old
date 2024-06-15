package freelanceplatform.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import freelanceplatform.controllers.handler.RestExceptionHandler;
import freelanceplatform.environment.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static freelanceplatform.environment.Environment.createDefaultMessageConverter;
import static freelanceplatform.environment.Environment.createStringEncodingMessageConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseControllerTest {
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    public void setUp(Object controller) {
        this.objectMapper = Environment.getObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        // Standalone setup initializes just the specified controller, without any security or services
        // We also provide the exception handler and message converters, so that error and data handling works
        // the same as usual
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new RestExceptionHandler())
                .setMessageConverters(createDefaultMessageConverter(),
                        createStringEncodingMessageConverter())
                .build();
    }

    String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    <T> T readValue(MvcResult result, Class<T> targetType) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), targetType);
    }

    <T> T readValue(MvcResult result, TypeReference<T> targetType) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), targetType);
    }

    void verifyLocationEquals(String expectedPath, MvcResult result) {
        final String locationHeader = result.getResponse().getHeader(HttpHeaders.LOCATION);
        assertEquals("http://localhost" + expectedPath, locationHeader);
    }
}
