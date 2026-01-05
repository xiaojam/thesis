package id.go.kemenag.spn.dto.document.marriage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarriageDocumentGroomDto {
    private String fullName;
    private String identityId;
    private String gender;
    private String birth;
    private String nationality;
    private String maritalStatus;
    private String religion;
    private String job;
    private String address;
    private String previousPartnerName;
    private String signName;
}
