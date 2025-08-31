package id.go.kemenag.spn.dto.user.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.AuthConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @JsonProperty("username")
    @NotBlank
    private String username;

    @JsonProperty("password")
    @NotBlank
    private String password;

    @JsonProperty("verify_password")
    @NotBlank
    private String verifyPassword;

    @JsonProperty("first_name")
    @NotBlank
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("role")
    @NotBlank
    private AuthConstant.Role role;

    @JsonProperty("workplace_code")
    @NotBlank
    private String workplaceCode;

    @JsonProperty("workplace_name")
    @NotBlank
    private String workplaceName;

}
