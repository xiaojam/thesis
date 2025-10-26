package id.go.kemenag.spn.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrideDocumentDataDto {
    private String subDistrictName;
    private String districtName;
    private String cityName;
    private String headVillageName;
    private String documentNumber;
    private String createdDate;
}
