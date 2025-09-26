package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.entity.divorce.MarriageData;
import id.go.kemenag.spn.repository.divorce.MarriageDataRepository;
import id.go.kemenag.spn.service.divorce.MarriageDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MarriageDataServiceImpl implements MarriageDataService {

    @Autowired
    private MarriageDataRepository marriageDataRepository;

    @Override
    public MarriageData save(MarriageData marriageData) {
        return this.marriageDataRepository.save(marriageData);
    }
}
