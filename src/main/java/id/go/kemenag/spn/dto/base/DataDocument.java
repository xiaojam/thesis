package id.go.kemenag.spn.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.DocumentConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataDocument {

    @JsonProperty("document_name")
    private String documentName;

    @JsonProperty("document_type")
    private DocumentConstant.DocumentType documentType;

    @JsonProperty("document_id")
    private String documentId;
}
