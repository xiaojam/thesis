package id.go.kemenag.spn.dto.document.marriage;

import id.go.kemenag.spn.dto.document.updatehistory.UpdateHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private List<UpdateHistoryDto> histories;
}
