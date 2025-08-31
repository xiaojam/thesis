package id.go.kemenag.spn.service.marriage;

import id.go.kemenag.spn.entity.marriage.Bride;
import id.go.kemenag.spn.entity.marriage.BrideFather;
import id.go.kemenag.spn.entity.marriage.BrideMother;

import java.util.List;
import java.util.UUID;

public interface BrideService {

    Bride save(Bride bride);

    BrideFather save(BrideFather brideFather);

    BrideMother save(BrideMother brideMother);

    Bride findFirstByIdentityId(String identityId);

    List<Bride> findAllByApplicationIds(List<UUID> applicationIds);
}
