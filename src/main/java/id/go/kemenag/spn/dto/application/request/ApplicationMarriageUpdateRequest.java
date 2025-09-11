package id.go.kemenag.spn.dto.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.dto.bride.request.*;
import id.go.kemenag.spn.dto.groom.request.*;
import id.go.kemenag.spn.dto.guardian.request.GuardianUpdateRequest;
import id.go.kemenag.spn.dto.marriage.request.MarriageUpdateRequest;
import id.go.kemenag.spn.dto.previouspartner.request.PreviousPartnerUpdateRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationMarriageUpdateRequest {

    @JsonProperty("bride")
    @NotNull
    private BrideUpdateRequest bride;

    @JsonProperty("bride_father")
    @NotNull
    private BrideFatherUpdateRequest brideFather;

    @JsonProperty("bride_mother")
    @NotNull
    private BrideMotherUpdateRequest brideMother;

    @JsonProperty("groom")
    @NotNull
    private GroomUpdateRequest groom;

    @JsonProperty("groom_father")
    @NotNull
    private GroomFatherUpdateRequest groomFather;

    @JsonProperty("groom_mother")
    @NotNull
    private GroomMotherUpdateRequest groomMother;

    @JsonProperty("guardian")
    @NotNull
    private GuardianUpdateRequest guardian;

    @JsonProperty("previous_groom_partner")
    private PreviousPartnerUpdateRequest previousGroomPartner;

    @JsonProperty("previous_bride_partner")
    private PreviousPartnerUpdateRequest previousBridePartner;

    @JsonProperty("marriage")
    private MarriageUpdateRequest marriage;
}
