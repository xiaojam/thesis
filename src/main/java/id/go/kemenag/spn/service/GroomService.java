package id.go.kemenag.spn.service;

import id.go.kemenag.spn.entity.marriage.Groom;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import id.go.kemenag.spn.entity.marriage.GroomMother;

public interface GroomService {

    Groom save(Groom groom);

    GroomMother save(GroomMother groomMother);

    GroomFather save(GroomFather groomFather);

    Groom findFirstByIdentityId(String identityId);
}
