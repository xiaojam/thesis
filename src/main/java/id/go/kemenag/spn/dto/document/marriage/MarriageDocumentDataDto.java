package id.go.kemenag.spn.dto.document.marriage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarriageDocumentDataDto {
    private String date;
    private String place;
    private String dowry;
}
