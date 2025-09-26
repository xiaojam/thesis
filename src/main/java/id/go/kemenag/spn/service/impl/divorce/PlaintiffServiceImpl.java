package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.entity.divorce.Plaintiff;
import id.go.kemenag.spn.repository.divorce.PlaintiffRepository;
import id.go.kemenag.spn.service.divorce.PlaintiffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlaintiffServiceImpl implements PlaintiffService {

    @Autowired
    private PlaintiffRepository plaintiffRepository;

    @Override
    public Plaintiff save(Plaintiff plaintiff) {
        return this.plaintiffRepository.save(plaintiff);
    }
}
