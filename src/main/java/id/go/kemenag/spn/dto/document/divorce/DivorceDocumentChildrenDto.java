package id.go.kemenag.spn.dto.document.divorce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceDocumentChildrenDto {
    private String fullName;
    private String gender;
    private String age;
}
