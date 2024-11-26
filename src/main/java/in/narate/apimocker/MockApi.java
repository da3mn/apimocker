package in.narate.apimocker;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MockApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String endpoint;
    private String method;
    private boolean ignoreFields;

    @Lob
    private String requestBody;

    @Lob
    private String responseBody;

    private String contentType;

}

