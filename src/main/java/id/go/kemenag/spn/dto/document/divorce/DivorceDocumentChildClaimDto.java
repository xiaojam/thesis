package id.go.kemenag.spn.dto.document.divorce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceDocumentChildClaimDto {
    private String childCount;
    private String monthlySupport;
    private List<DivorceDocumentChildrenDto> children;
}
