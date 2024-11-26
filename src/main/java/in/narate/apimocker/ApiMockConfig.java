package in.narate.apimocker;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ApiMockConfig {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String endpoint;
        private String requestType;
        private String requestBody;
        private String responseBody;
        private String responseType;
        private String originalServerUrl;

        public ApiMockConfig() {
        }

        public ApiMockConfig(Long id, String endpoint, String requestType, String requestBody, String responseBody, String responseType, String originalServerUrl) {
                this.id = id;
                this.endpoint = endpoint;
                this.requestType = requestType;
                this.requestBody = requestBody;
                this.responseBody = responseBody;
                this.responseType = responseType;
                this.originalServerUrl = originalServerUrl;
        }

        public String getOriginalServerUrl() {
                return originalServerUrl;
        }

        public void setOriginalServerUrl(String originalServerUrl) {
                this.originalServerUrl = originalServerUrl;
        }
}
