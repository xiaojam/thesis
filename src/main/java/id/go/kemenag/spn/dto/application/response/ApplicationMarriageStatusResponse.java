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
public class ApplicationMarriageStatusResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("groom_name")
    private String groomName;

    @JsonProperty("bride_name")
    private String brideName;
}
