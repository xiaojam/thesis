package id.go.kemenag.spn.dto.document.marriage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarriageDocumentGroomFatherDto {
    private String fullName;
    private String fatherName;
    private String identityId;
    private String gender;
    private String birth;
    private String nationality;
    private String religion;
    private String job;
    private String address;
    private String signName;
}
