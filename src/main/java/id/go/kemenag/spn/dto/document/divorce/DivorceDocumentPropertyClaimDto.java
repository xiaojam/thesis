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
public class DivorceDocumentPropertyClaimDto {
    private String divisionRequest;
    private List<DivorceDocumentPropertiesDto> properties;
}
