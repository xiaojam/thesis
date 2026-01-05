package id.go.kemenag.spn.dto.divorce.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceReasonCreateRequest {

    @JsonProperty("conflict_start_date")
    private LocalDate conflictStartDate;

    @JsonProperty("conflict_causes")
    private List<String> conflictCauses;

    @JsonProperty("conflict_climax_date")
    private LocalDate conflictClimaxDate;

    @JsonProperty("separation_date")
    private LocalDate separationDate;
}
