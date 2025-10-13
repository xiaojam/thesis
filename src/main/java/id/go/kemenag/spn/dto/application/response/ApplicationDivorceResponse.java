package id.go.kemenag.spn.dto.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.dto.caseschedule.response.CaseScheduleResponse;
import id.go.kemenag.spn.dto.child.response.ChildClaimResponse;
import id.go.kemenag.spn.dto.defendant.response.DefendantResponse;
import id.go.kemenag.spn.dto.divorce.response.DivorceReasonResponse;
import id.go.kemenag.spn.dto.marriage.response.MarriageDataResponse;
import id.go.kemenag.spn.dto.plaintiff.response.PlaintiffResponse;
import id.go.kemenag.spn.dto.property.response.PropertyClaimResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDivorceResponse {

    @JsonProperty("application_id")
    private UUID applicationId;

    @JsonProperty("status")
    private ApplicationConstant.Status status;

    @JsonProperty("process_id")
    private UUID processId;

    @JsonProperty("case_number")
    private String caseNumber;

    @JsonProperty("court_code")
    private String courtCode;

    @JsonProperty("court_name")
    private String courtName;

    @JsonProperty("plaintiff")
    private PlaintiffResponse plaintiff;

    @JsonProperty("defendant")
    private DefendantResponse defendant;

    @JsonProperty("marriage_data")
    private MarriageDataResponse marriageData;

    @JsonProperty("divorce_reason")
    private DivorceReasonResponse divorceReason;

    @JsonProperty("child_claim")
    private ChildClaimResponse childClaim;

    @JsonProperty("property_claim")
    private PropertyClaimResponse propertyClaim;

    @JsonProperty("schedules")
    private List<CaseScheduleResponse> schedules;
}
