package id.go.kemenag.spn.dto.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.dto.bride.request.BrideCreateRequest;
import id.go.kemenag.spn.dto.bride.request.BrideFatherCreateRequest;
import id.go.kemenag.spn.dto.bride.request.BrideMotherCreateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomCreateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomFatherCreateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomMotherCreateRequest;
import id.go.kemenag.spn.dto.guardian.request.GuardianCreateRequest;
import id.go.kemenag.spn.dto.previouspartner.request.PreviousPartnerCreateRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationCreateRequest {

    @JsonProperty("bride")
    @NotNull
    private BrideCreateRequest bride;

    @JsonProperty("bride_father")
    @NotNull
    private BrideFatherCreateRequest brideFather;

    @JsonProperty("bride_mother")
    @NotNull
    private BrideMotherCreateRequest brideMother;

    @JsonProperty("groom")
    @NotNull
    private GroomCreateRequest groom;

    @JsonProperty("groom_father")
    @NotNull
    private GroomFatherCreateRequest groomFather;

    @JsonProperty("groom_mother")
    @NotNull
    private GroomMotherCreateRequest groomMother;

    @JsonProperty("guardian")
    @NotNull
    private GuardianCreateRequest guardian;

    @JsonProperty("previous_groom_partner")
    private PreviousPartnerCreateRequest previousGroomPartner;

    @JsonProperty("previous_bride_partner")
    private PreviousPartnerCreateRequest previousBridePartner;
}
