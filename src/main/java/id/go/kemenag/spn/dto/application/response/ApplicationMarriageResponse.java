package id.go.kemenag.spn.dto.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.dto.bride.response.BrideFatherResponse;
import id.go.kemenag.spn.dto.bride.response.BrideMotherResponse;
import id.go.kemenag.spn.dto.bride.response.BrideResponse;
import id.go.kemenag.spn.dto.groom.response.GroomFatherResponse;
import id.go.kemenag.spn.dto.groom.response.GroomMotherResponse;
import id.go.kemenag.spn.dto.groom.response.GroomResponse;
import id.go.kemenag.spn.dto.guardian.response.GuardianResponse;
import id.go.kemenag.spn.dto.marriage.response.MarriageResponse;
import id.go.kemenag.spn.dto.previouspartner.response.PreviousPartnerResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationMarriageResponse {

    @JsonProperty("application_id")
    private UUID applicationId;

    @JsonProperty("status")
    private ApplicationConstant.Status status;

    @JsonProperty("process_id")
    private UUID processId;

    @JsonProperty("bride")
    private BrideResponse bride;

    @JsonProperty("bride_father")
    private BrideFatherResponse brideFather;

    @JsonProperty("bride_mother")
    private BrideMotherResponse brideMother;

    @JsonProperty("guardian")
    private GuardianResponse guardian;

    @JsonProperty("previous_bride_partner")
    private PreviousPartnerResponse previousBridePartner;

    @JsonProperty("groom")
    private GroomResponse groom;

    @JsonProperty("groom_father")
    private GroomFatherResponse groomFather;

    @JsonProperty("groom_mother")
    private GroomMotherResponse groomMother;

    @JsonProperty("previous_groom_partner")
    private PreviousPartnerResponse previousGroomPartner;

    @JsonProperty("marriage")
    private MarriageResponse marriage;
}
