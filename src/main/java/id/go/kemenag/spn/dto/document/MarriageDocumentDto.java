package id.go.kemenag.spn.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarriageDocumentDto {

    private MarriageDocumentGroomDto groom;

    private MarriageDocumentGroomMotherDto groomMother;

    private MarriageDocumentGroomFatherDto groomFather;

    private MarriageDocumentGroomPreviousPartnerDto groomPreviousPartner;

    private MarriageDocumentBrideDto bride;

    private MarriageDocumentBrideMotherDto brideMother;

    private MarriageDocumentBrideFatherDto brideFather;

    private MarriageDocumentBridePreviousPartnerDto bridePreviousPartner;

    private MarriageDocumentGuardianDto guardian;

    private MarriageDocumentDataDto marriageData;

    private GroomDocumentDataDto groomDocumentData;

    private BrideDocumentDataDto brideDocumentData;
}
