package id.go.kemenag.spn.service.master;

import id.go.kemenag.spn.entity.master.Master;

import java.util.List;
import java.util.UUID;

public interface MasterService {

    Master findByGroupNameAndCode(String groupName, String code);
}
