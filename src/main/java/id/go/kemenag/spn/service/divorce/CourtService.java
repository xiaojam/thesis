package id.go.kemenag.spn.service.divorce;

import id.go.kemenag.spn.entity.master.Master;

public interface CourtService {

    Master findCourtByCityCode(String cityCode);
}
