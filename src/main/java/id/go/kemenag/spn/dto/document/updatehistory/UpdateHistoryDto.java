package id.go.kemenag.spn.dto.document.updatehistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateHistoryDto {
    private String label;
    private String oldValue;
    private String newValue;
    private String time;
    private String handler;
}
