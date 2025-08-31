package id.go.kemenag.spn.service.marriage;

import id.go.kemenag.spn.entity.marriage.Groom;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import id.go.kemenag.spn.entity.marriage.GroomMother;

import java.util.List;
import java.util.UUID;

public interface GroomService {

    Groom save(Groom groom);

    GroomMother save(GroomMother groomMother);

    GroomFather save(GroomFather groomFather);

    Groom findFirstByIdentityId(String identityId);

    List<Groom> findAllByApplicationIds(List<UUID> applicationIds);
}
