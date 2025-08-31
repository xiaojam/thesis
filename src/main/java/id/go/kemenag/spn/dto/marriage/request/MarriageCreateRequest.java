package id.go.kemenag.spn.dto.marriage.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarriageCreateRequest {

    @JsonProperty("datetime")
    private LocalDateTime datetime;

    @JsonProperty("dowry")
    private String dowry;

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
