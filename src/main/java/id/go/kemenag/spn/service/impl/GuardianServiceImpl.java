package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.entity.marriage.Guardian;
import id.go.kemenag.spn.repository.marriage.GuardianRepository;
import id.go.kemenag.spn.service.GuardianService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GuardianServiceImpl implements GuardianService {

    @Autowired
    private GuardianRepository guardianRepository;

    @Override
    public Guardian save(Guardian guardian) {
        return this.guardianRepository.save(guardian);
    }
}
