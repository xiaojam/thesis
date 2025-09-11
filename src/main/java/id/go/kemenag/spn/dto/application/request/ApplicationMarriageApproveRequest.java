package id.go.kemenag.spn.dto.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.ApplicationConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationMarriageApproveRequest {

    @JsonProperty("application_id")
    private UUID applicationId;

    @JsonProperty("approved_status")
    private ApplicationConstant.ApprovedStatus approvedStatus;
}
