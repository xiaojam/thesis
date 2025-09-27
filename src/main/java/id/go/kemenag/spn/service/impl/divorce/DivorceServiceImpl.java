package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.entity.divorce.DivorceCase;
import id.go.kemenag.spn.entity.divorce.DivorceReason;
import id.go.kemenag.spn.repository.divorce.DivorceCaseRepository;
import id.go.kemenag.spn.repository.divorce.DivorceReasonRepository;
import id.go.kemenag.spn.service.divorce.DivorceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DivorceServiceImpl implements DivorceService {

    @Autowired
    private DivorceCaseRepository divorceCaseRepository;

    @Autowired
    private DivorceReasonRepository divorceReasonRepository;

    @Override
    public DivorceCase save(DivorceCase divorceCase) {
        return this.divorceCaseRepository.save(divorceCase);
    }

    @Override
    public DivorceReason save(DivorceReason divorceReason) {
        return this.divorceReasonRepository.save(divorceReason);
    }
}
