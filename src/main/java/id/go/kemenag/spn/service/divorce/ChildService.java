package id.go.kemenag.spn.service.divorce;

import id.go.kemenag.spn.entity.divorce.Child;
import id.go.kemenag.spn.entity.divorce.ChildClaim;

public interface ChildService {

    ChildClaim save(ChildClaim childClaim);

    Child save(Child child);
}
