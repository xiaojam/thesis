package id.go.kemenag.spn.dto.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.DivorceConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDivorceDoneRequest {

    @JsonProperty("application_id")
    private UUID applicationId;

    @JsonProperty("date_type")
    private DivorceConstant.SetDateType dateType;

    @JsonProperty("is_plaintiff_present")
    private Boolean isPlaintiffPresent;

    @JsonProperty("is_defendant_present")
    private Boolean isDefendantPresent;

    @JsonProperty("is_reconciliation_success")
    private Boolean isReconciliationSuccess;
}
