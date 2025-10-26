package id.go.kemenag.spn.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarriageDocumentBridePreviousPartnerDto {
    private String fullName;
    private String fatherName;
    private String identityId;
    private String birth;
    private String deathDate;
    private String deathPlace;
    private String nationality;
    private String religion;
    private String job;
    private String address;
}
