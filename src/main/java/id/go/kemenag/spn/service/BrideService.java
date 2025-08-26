package id.go.kemenag.spn.service;

import id.go.kemenag.spn.entity.marriage.Bride;
import id.go.kemenag.spn.entity.marriage.BrideFather;
import id.go.kemenag.spn.entity.marriage.BrideMother;

public interface BrideService {

    Bride save(Bride bride);

    BrideFather save(BrideFather brideFather);

    BrideMother save(BrideMother brideMother);
}
