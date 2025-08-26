package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.entity.marriage.Groom;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import id.go.kemenag.spn.entity.marriage.GroomMother;
import id.go.kemenag.spn.repository.marriage.GroomFatherRepository;
import id.go.kemenag.spn.repository.marriage.GroomMotherRepository;
import id.go.kemenag.spn.repository.marriage.GroomRepository;
import id.go.kemenag.spn.service.GroomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
