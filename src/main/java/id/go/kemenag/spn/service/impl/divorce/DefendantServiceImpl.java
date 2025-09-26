package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.entity.divorce.Defendant;
import id.go.kemenag.spn.repository.divorce.DefendantRepository;
import id.go.kemenag.spn.service.divorce.DefendantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefendantServiceImpl implements DefendantService {

    @Autowired
    private DefendantRepository defendantRepository;

    @Override
    public Defendant save(Defendant defendant) {
        return this.defendantRepository.save(defendant);
    }
}
