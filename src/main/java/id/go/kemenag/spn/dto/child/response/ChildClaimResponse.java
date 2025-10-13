package id.go.kemenag.spn.dto.child.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.dto.child.request.ChildCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildClaimResponse {

    @JsonProperty("custody_request")
    private String custodyRequest;

    @JsonProperty("monthly_support")
    private Double monthlySupport;

    @JsonProperty("children")
    private List<ChildResponse> children;
}
