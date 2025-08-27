package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.entity.marriage.Marriage;
import id.go.kemenag.spn.repository.marriage.MarriageRepository;
import id.go.kemenag.spn.service.MarriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarriageServiceImpl implements MarriageService {

    @Autowired
    private MarriageRepository marriageRepository;

    @Override
    public Marriage save(Marriage marriage) {
        return this.marriageRepository.save(marriage);
    }
}
