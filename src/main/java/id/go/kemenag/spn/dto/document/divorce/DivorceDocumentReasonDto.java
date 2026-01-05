package id.go.kemenag.spn.dto.document.divorce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceDocumentReasonDto {
    private String conflictStartDate;
    private String conflictClimaxDate;
    private String separationDate;
    private List<String> conflictCauses;
    private String reconciliationAttemptDescription;
    private String totalSeparationDuration;
}
