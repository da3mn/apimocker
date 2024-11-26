package in.narate.apimocker;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiMockRepository extends JpaRepository<ApiMockConfig, Long> {
    ApiMockConfig findByEndpoint(String endpoint);
}

