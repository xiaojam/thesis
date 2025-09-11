package id.go.kemenag.spn.dto.guardian.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.MarriageConstant;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuardianCreateRequest {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("identity_id")
    @Size(min = 16, max = 16)
    private String identityId;

    @JsonProperty("birth_place")
    private String birthPlace;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @JsonProperty("gender")
    private MarriageConstant.Gender gender;

    @JsonProperty("job")
    private String job;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("religion")
    private MarriageConstant.Religion religion;

    @JsonProperty("marital_status")
    private MarriageConstant.MaritalStatus maritalStatus;

    @JsonProperty("status")
    private MarriageConstant.GuardianStatus status;

    @JsonProperty("father_name")
    private String fatherName;

    @JsonProperty("province_code")
    private String provinceCode;

    @JsonProperty("province_name")
    private String provinceName;

    @JsonProperty("city_code")
    private String cityCode;

    @JsonProperty("city_name")
    private String cityName;

    @JsonProperty("district_code")
    private String districtCode;

    @JsonProperty("district_name")
    private String districtName;

    @JsonProperty("sub_district_code")
    private String subDistrictCode;

    @JsonProperty("sub_district_name")
    private String subDistrictName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("rw")
    @Size(min = 3, max = 3)
    private String rw;

    @JsonProperty("rt")
    @Size(min = 3, max = 3)
    private String rt;

    @JsonProperty("zip_code")
    private String zipCode;
}
