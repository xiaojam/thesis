package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.entity.master.Master;
import id.go.kemenag.spn.service.divorce.CourtService;
import id.go.kemenag.spn.service.master.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static id.go.kemenag.spn.util.MasterUtil.getReligiousCourt;

@Service
public class CourtServiceImpl implements CourtService {

    @Autowired
    private MasterService masterService;

    @Override
    public Master findCourtByCityCode(String cityCode) {
        Master cityMaster = masterService.findByGroupNameAndCode("WILAYAH", cityCode);

        if (cityMaster == null || cityMaster.getChildren() == null || cityMaster.getChildren().isEmpty()) {
            return null;
        }

        return getReligiousCourt(cityMaster.getChildren());
    }
}
