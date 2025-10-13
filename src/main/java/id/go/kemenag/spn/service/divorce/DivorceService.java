package id.go.kemenag.spn.service.divorce;

import id.go.kemenag.spn.entity.divorce.DivorceCase;
import id.go.kemenag.spn.entity.divorce.DivorceReason;

import java.util.List;
import java.util.UUID;

public interface DivorceService {

    DivorceCase save(DivorceCase divorceCase);

    DivorceReason save(DivorceReason divorceReason);

    List<DivorceCase> findAllByApplicationIds(List<UUID> applicationIds);

    DivorceCase findByApplicationId(UUID applicationId);

    DivorceCase findByCaseNumber(String caseNumber);
}
