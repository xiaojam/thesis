package id.go.kemenag.spn.dto.document.divorce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceDocumentDto {
    private String caseTitle;
    private String caseType;
    private String courtName;
    private String courtCity;
    private DivorceDocumentPlaintiffDto plaintiff;
    private DivorceDocumentDefendantDto defendant;
    private DivorceDocumentMarriageDataDto marriageData;
    private DivorceDocumentReasonDto divorceReason;
    private String iddahSupportAmount;
    private String mutahDescription;
    private String maddiyahSupportAmount;
    private String maddiyahDurationInMonths;
    private DivorceDocumentChildClaimDto childClaim;
    private DivorceDocumentPropertyClaimDto propertyClaim;
}
