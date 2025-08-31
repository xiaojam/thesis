package id.go.kemenag.spn.service.impl.marriage;

import id.go.kemenag.spn.entity.marriage.PreviousPartner;
import id.go.kemenag.spn.repository.marriage.PreviousPartnerRepository;
import id.go.kemenag.spn.service.marriage.PreviousPartnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PreviousPartnerServiceImpl implements PreviousPartnerService {

    @Autowired
    private PreviousPartnerRepository previousPartnerRepository;

    @Override
    public PreviousPartner save(PreviousPartner previousPartner) {
        return this.previousPartnerRepository.save(previousPartner);
    }
}
