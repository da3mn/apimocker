package in.narate.apimocker;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

@RestController
@RequestMapping("/mock-config")
public class ApiMockRequestController {

    @Autowired
    private ApiMockService apiMockService;

    private static final Logger logger = LoggerFactory.getLogger(ApiMockRequestController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/{endpoint}/**")
    public ResponseEntity<?> handleRequest(
            @PathVariable String endpoint,
            HttpServletRequest request,
            @RequestBody(required = false) String body
    ) {
        logger.info("Received request for endpoint: " + endpoint);
        ApiMockConfig mockConfig = apiMockService.findMockConfigByEndpoint(endpoint);

        if (mockConfig == null) {
            logger.warn("No mock configuration found for this endpoint: " + endpoint);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No mock configuration found for this endpoint.");
        }

        logger.info("Mock configuration found: " + mockConfig);

        String contentType = request.getContentType();
        if (contentType != null) {
            if (contentType.contains("application/json")) {
                return handleJsonRequest(mockConfig, body);
            } else if (contentType.contains("application/xml")) {
                return handleXmlRequest(mockConfig, body);
            }
        }

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body("Unsupported content type. Please send either JSON or XML.");
    }

    private ResponseEntity<?> handleJsonRequest(ApiMockConfig mockConfig, String body) {
        try {
            Object parsedRequestBody = objectMapper.readTree(body);
            Object parsedMockRequestBody = objectMapper.readTree(mockConfig.getRequestBody());

            if (parsedRequestBody.equals(parsedMockRequestBody)) {
                return ResponseEntity.ok(mockConfig.getResponseBody());
            }

            if (mockConfig.getOriginalServerUrl() != null) {
                return apiMockService.forwardToOriginalServer(mockConfig.getOriginalServerUrl(), null, body);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mock not found or no matching request body.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error parsing JSON body: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleXmlRequest(ApiMockConfig mockConfig, String body) {
        try {
            MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter();
            Object parsedRequestBody = xmlConverter.getObjectMapper().readTree(body);
            Object parsedMockRequestBody = xmlConverter.getObjectMapper().readTree(mockConfig.getRequestBody());

            if (parsedRequestBody.equals(parsedMockRequestBody)) {
                return ResponseEntity.ok(mockConfig.getResponseBody());
            }

            if (mockConfig.getOriginalServerUrl() != null) {
                return apiMockService.forwardToOriginalServer(mockConfig.getOriginalServerUrl(), null, body);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mock not found or no matching request body.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error parsing XML body: " + e.getMessage());
        }
    }
}
