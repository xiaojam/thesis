package id.go.kemenag.spn.service.divorce;

import id.go.kemenag.spn.entity.divorce.PropertyClaim;
import id.go.kemenag.spn.entity.divorce.SharedProperty;

public interface PropertyService {

    SharedProperty save(SharedProperty sharedProperty);

    PropertyClaim save(PropertyClaim propertyClaim);
}
