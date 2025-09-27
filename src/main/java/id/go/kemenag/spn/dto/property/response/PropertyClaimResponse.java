package id.go.kemenag.spn.dto.property.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.dto.property.request.SharedPropertyCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyClaimResponse {

    @JsonProperty("division_request")
    private String divisionRequest;

    @JsonProperty("properties")
    private List<SharedPropertyCreateRequest> properties;
}
