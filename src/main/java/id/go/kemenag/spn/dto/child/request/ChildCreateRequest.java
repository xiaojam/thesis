package id.go.kemenag.spn.dto.child.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildCreateRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("in_custody_of_plaintiff")
    private Boolean inCustodyOfPlaintiff;
}
