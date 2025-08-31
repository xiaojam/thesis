package id.go.kemenag.spn.service.impl.marriage;

import id.go.kemenag.spn.entity.marriage.Bride;
import id.go.kemenag.spn.entity.marriage.BrideFather;
import id.go.kemenag.spn.entity.marriage.BrideMother;
import id.go.kemenag.spn.mapper.BrideFatherMapper;
import id.go.kemenag.spn.repository.marriage.BrideMotherRepository;
import id.go.kemenag.spn.repository.marriage.BrideRepository;
import id.go.kemenag.spn.service.marriage.BrideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class BrideServiceImpl implements BrideService {

    @Autowired
    private BrideRepository brideRepository;

    @Autowired
    private BrideFatherMapper brideFatherMapper;

    @Autowired
    private BrideMotherRepository brideMotherRepository;

    @Override
    public Bride save(Bride bride) {
        return this.brideRepository.save(bride);
    }

    @Override
    public BrideFather save(BrideFather brideFather) {
        return null;
    }

    @Override
    public BrideMother save(BrideMother brideMother) {
        return null;
    }

    @Override
    public Bride findFirstByIdentityId(String identityId) {
        return this.brideRepository.findFirstByIdentityIdAndDeletedIsFalseOrderByCreatedAtDesc(identityId).orElse(null);
    }

    @Override
    public List<Bride> findAllByApplicationIds(List<UUID> applicationIds) {
        return Streamable
            .of(this.brideRepository.findAllByApplicationIdInAndDeletedIsFalseOrderByCreatedAtAsc(applicationIds))
            .stream()
            .toList();
    }
}
