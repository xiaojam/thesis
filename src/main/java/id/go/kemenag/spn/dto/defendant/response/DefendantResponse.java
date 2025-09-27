package id.go.kemenag.spn.dto.defendant.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.DivorceConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefendantResponse {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("birth_place")
    private String birthPlace;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("religion")
    private DivorceConstant.Religion religion;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("identity_type")
    private DivorceConstant.IdentityType identityType;

    @JsonProperty("identity_number")
    private String identityNumber;

    @JsonProperty("marital_status")
    private DivorceConstant.MaritalStatus maritalStatus;

    @JsonProperty("education")
    private String education;

    @JsonProperty("job")
    private String job;

    @JsonProperty("phone_number")
    private String phoneNumber;

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
    private String rw;

    @JsonProperty("rt")
    private String rt;

    @JsonProperty("zip_code")
    private String zipCode;
}
