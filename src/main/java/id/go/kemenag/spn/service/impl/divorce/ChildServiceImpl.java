package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.entity.divorce.Child;
import id.go.kemenag.spn.entity.divorce.ChildClaim;
import id.go.kemenag.spn.repository.divorce.ChildClaimRepository;
import id.go.kemenag.spn.repository.divorce.ChildRepository;
import id.go.kemenag.spn.service.divorce.ChildService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChildServiceImpl implements ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ChildClaimRepository childClaimRepository;

    @Override
    public ChildClaim save(ChildClaim childClaim) {
        return this.childClaimRepository.save(childClaim);
    }

    @Override
    public Child save(Child child) {
        return this.childRepository.save(child);
    }
}
