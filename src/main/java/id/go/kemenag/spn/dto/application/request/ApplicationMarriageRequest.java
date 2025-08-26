package id.go.kemenag.spn.dto.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationMarriageRequest {

    @JsonProperty("groom_identity_id" )
    @Size(min = 16, max = 16)
    private String groomIdentityId;

    @JsonProperty("bride_identity_id" )
    @Size(min = 16, max = 16)
    private String brideIdentityId;
}
