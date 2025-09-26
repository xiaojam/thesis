package id.go.kemenag.spn.dto.divorce.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceReasonCreateRequest {

    @JsonProperty("initial_situation")
    private String initialSituation;

    @JsonProperty("conflict_reason")
    private String conflictReason;

    @JsonProperty("reconciliation_attempt")
    private String reconciliationAttempt;

    @JsonProperty("current_condition")
    private String currentCondition;
}
