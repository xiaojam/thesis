package id.go.kemenag.spn.service.divorce;

import id.go.kemenag.spn.entity.divorce.DivorceCase;
import id.go.kemenag.spn.entity.divorce.DivorceReason;

public interface DivorceService {

    DivorceCase save(DivorceCase divorceCase);

    DivorceReason save(DivorceReason divorceReason);
}
