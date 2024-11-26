package in.narate.apimocker;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ApiMockService {

    @Autowired
    private ApiMockRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(ApiMockService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    public void addApiMockConfig(ApiMockConfig config) {
        repository.save(config);
    }



    public ApiMockConfig findMockConfigById(Long id) {
        Optional<ApiMockConfig> mockConfig = repository.findById(id);
        return mockConfig.orElse(null); // Return the config if it exists, or null if not found
    }

    public ResponseEntity<?> forwardToOriginalServer(String serverUrl, HttpServletRequest request, String body) {
        HttpHeaders headers = new HttpHeaders();
        request.getHeaderNames().asIterator().forEachRemaining(headerName ->
                headers.add(headerName, request.getHeader(headerName))
        );

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            String url = serverUrl + request.getRequestURI();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.valueOf(request.getMethod()), entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error forwarding request to original server: " + e.getMessage());
        }
    }

    public List<ApiMockConfig> getAllMocks() {
        return repository.findAll();
    }

    public void deleteMockConfig(Long id) {
        repository.deleteById(id);
    }

    public void updateApiMockConfig(ApiMockConfig updatedConfig) {
        repository.save(updatedConfig);
    }
    public ApiMockConfig findMockConfigByEndpoint(String endpoint) {
        // Log all configurations to check
        List<ApiMockConfig> allConfigs = repository.findAll();
        for (ApiMockConfig config : allConfigs) {
            logger.info("Stored mock config: " + config.getEndpoint());
        }

        return repository.findByEndpoint(endpoint);
    }

}
