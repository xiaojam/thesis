package id.go.kemenag.spn.dto.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.dto.child.request.ChildClaimCreateRequest;
import id.go.kemenag.spn.dto.defendant.request.DefendantCreateRequest;
import id.go.kemenag.spn.dto.divorce.request.DivorceReasonCreateRequest;
import id.go.kemenag.spn.dto.marriage.request.MarriageDataCreateRequest;
import id.go.kemenag.spn.dto.plaintiff.request.PlaintiffCreateRequest;
import id.go.kemenag.spn.dto.property.request.PropertyClaimCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDivorceCreateRequest {

    @JsonProperty("plaintiff")
    private PlaintiffCreateRequest plaintiff;

    @JsonProperty("defendant")
    private DefendantCreateRequest defendant;

    @JsonProperty("marriage_data")
    private MarriageDataCreateRequest marriageData;

    @JsonProperty("divorce_reason")
    private DivorceReasonCreateRequest divorceReason;

    @JsonProperty("child_claim")
    private ChildClaimCreateRequest childClaim;

    @JsonProperty("property_claim")
    private PropertyClaimCreateRequest propertyClaim;
}
