package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.entity.marriage.PreviousPartner;
import id.go.kemenag.spn.repository.divorce.PreviousPartnerRepository;
import id.go.kemenag.spn.service.PreviousPartnerService;
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
