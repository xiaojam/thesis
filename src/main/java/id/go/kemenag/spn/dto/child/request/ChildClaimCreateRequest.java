package id.go.kemenag.spn.dto.child.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildClaimCreateRequest {

    @JsonProperty("claimed")
    private Boolean claimed;

    @JsonProperty("custody_request")
    private String custodyRequest;

    @JsonProperty("monthly_support")
    private Double monthlySupport;

    @JsonProperty("children")
    private List<ChildCreateRequest> children;
}
