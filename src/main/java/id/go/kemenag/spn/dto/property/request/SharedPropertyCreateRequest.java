package id.go.kemenag.spn.dto.property.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedPropertyCreateRequest {

    @JsonProperty("property_type")
    private String propertyType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("estimated_value")
    private Double estimatedValue;

    @JsonProperty("ownership_proof")
    private String ownershipProof;
}
