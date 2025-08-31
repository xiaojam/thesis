package id.go.kemenag.spn.service.impl.marriage;

import id.go.kemenag.spn.entity.marriage.Groom;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import id.go.kemenag.spn.entity.marriage.GroomMother;
import id.go.kemenag.spn.repository.marriage.GroomFatherRepository;
import id.go.kemenag.spn.repository.marriage.GroomMotherRepository;
import id.go.kemenag.spn.repository.marriage.GroomRepository;
import id.go.kemenag.spn.service.marriage.GroomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GroomServiceImpl implements GroomService {

    @Autowired
    private GroomRepository groomRepository;

    @Autowired
    private GroomFatherRepository groomFatherRepository;

    @Autowired
    private GroomMotherRepository groomMotherRepository;

    @Override
    public Groom save(Groom groom) {
        return this.groomRepository.save(groom);
    }

    @Override
    public GroomMother save(GroomMother groomMother) {
        return this.groomMotherRepository.save(groomMother);
    }

    @Override
    public GroomFather save(GroomFather groomFather) {
        return this.groomFatherRepository.save(groomFather);
    }

    @Override
    public Groom findFirstByIdentityId(String identityId) {
        return this.groomRepository.findFirstByIdentityIdAndDeletedIsFalseOrderByCreatedAtDesc(identityId).orElse(null);
    }

    @Override
    public List<Groom> findAllByApplicationIds(List<UUID> applicationIds) {
        return Streamable
            .of(this.groomRepository.findAllByApplicationIdInAndDeletedIsFalseOrderByCreatedAtAsc(applicationIds))
            .stream()
            .toList();
    }
}
