package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.entity.marriage.Bride;
import id.go.kemenag.spn.repository.divorce.BrideRepository;
import id.go.kemenag.spn.service.BrideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrideServiceImpl implements BrideService {

    @Autowired
    private BrideRepository brideRepository;

    @Override
    public void save(Bride bride) {
        this.brideRepository.save(bride);
    }
}
