package id.go.kemenag.spn.dto.divorce.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceReasonResponse {

    @JsonProperty("initial_situation")
    private String initialSituation;

    @JsonProperty("conflict_reason")
    private String conflictReason;

    @JsonProperty("reconciliation_attempt")
    private String reconciliationAttempt;

    @JsonProperty("current_condition")
    private String currentCondition;
}
