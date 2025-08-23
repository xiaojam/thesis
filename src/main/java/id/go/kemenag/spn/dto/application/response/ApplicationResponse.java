package id.go.kemenag.spn.dto.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {

    @JsonProperty("application_id")
    private String applicationId;

    @JsonProperty("process_id")
    private String processId;
}
