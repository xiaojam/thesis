package id.go.kemenag.spn.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.DivorceConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildData implements Serializable {

    @Serial
    private static final long serialVersionUID = -7552580381887275336L;

    @JsonProperty("gender")
    private DivorceConstant.Gender gender;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("birth_place")
    private String birthPlace;

    @JsonProperty("birth_date")
    private LocalDate birthDate;
}
