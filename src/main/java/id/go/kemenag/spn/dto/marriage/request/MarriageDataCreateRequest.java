package id.go.kemenag.spn.dto.marriage.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarriageDataCreateRequest {

    @JsonProperty("marriage_date")
    private LocalDate marriageDate;

    @JsonProperty("marriage_place")
    private String marriagePlace;

    @JsonProperty("marriage_certificate_number")
    private String marriageCertificateNumber;

    @JsonProperty("household_address")
    private String householdAddress;

    @JsonProperty("has_children")
    private Boolean hasChildren;
}
