package id.go.kemenag.spn.dto.document.divorce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceDocumentPlaintiffDto {
    private String fullName;
    private String identityId;
    private String birth;
    private String gender;
    private String religion;
    private String job;
    private String salary;
    private String education;
    private String phoneNumber;
    private String address;
    private String signName;
}
