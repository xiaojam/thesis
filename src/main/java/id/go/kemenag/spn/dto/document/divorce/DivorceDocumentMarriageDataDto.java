package id.go.kemenag.spn.dto.document.divorce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceDocumentMarriageDataDto {
    private String marriageCertificateNumber;
    private String marriageDate;
    private String marriagePlace;
    private String householdAddress;
}
