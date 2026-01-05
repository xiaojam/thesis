package id.go.kemenag.spn.dto.document.divorce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivorceDocumentPropertiesDto {
    private String propertyType;
    private String description;
    private String ownershipProof;
    private String estimatedValue;
}
