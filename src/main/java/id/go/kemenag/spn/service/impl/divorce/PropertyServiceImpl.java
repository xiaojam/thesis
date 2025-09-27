package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.entity.divorce.PropertyClaim;
import id.go.kemenag.spn.entity.divorce.SharedProperty;
import id.go.kemenag.spn.repository.divorce.PropertyClaimRepository;
import id.go.kemenag.spn.repository.divorce.SharedPropertyRepository;
import id.go.kemenag.spn.service.divorce.PropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    private PropertyClaimRepository propertyClaimRepository;

    @Autowired
    private SharedPropertyRepository sharedPropertyRepository;

    @Override
    public SharedProperty save(SharedProperty sharedProperty) {
        return this.sharedPropertyRepository.save(sharedProperty);
    }

    @Override
    public PropertyClaim save(PropertyClaim propertyClaim) {
        return this.propertyClaimRepository.save(propertyClaim);
    }
}
