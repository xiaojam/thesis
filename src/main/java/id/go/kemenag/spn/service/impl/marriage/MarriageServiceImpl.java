package id.go.kemenag.spn.service.impl.marriage;

import id.go.kemenag.spn.entity.marriage.Marriage;
import id.go.kemenag.spn.repository.marriage.MarriageRepository;
import id.go.kemenag.spn.service.marriage.MarriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MarriageServiceImpl implements MarriageService {

    @Autowired
    private MarriageRepository marriageRepository;

    @Override
    public Marriage save(Marriage marriage) {
        return this.marriageRepository.save(marriage);
    }

    @Override
    public List<Marriage> findAllByApplicationIds(List<UUID> applicationIds) {
        return Streamable
            .of(this.marriageRepository.findALlByApplicationIdInAndDeletedFalseOrderByCreatedAtAsc(applicationIds))
            .stream()
            .toList();
    }

    @Override
    public Marriage findByApplicationId(UUID applicationId) {
        return this.marriageRepository.findByApplicationIdAndDeletedFalse(applicationId).orElse(null);
    }
}
