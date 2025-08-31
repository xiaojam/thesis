package id.go.kemenag.spn.dto.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationMarriageCreateResponse {

    @JsonProperty("application_id")
    private UUID applicationId;

    @JsonProperty("process_id")
    private UUID processId;
}
