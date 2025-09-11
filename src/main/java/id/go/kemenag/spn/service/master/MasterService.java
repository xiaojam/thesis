package id.go.kemenag.spn.service.master;

import id.go.kemenag.spn.entity.master.Master;

import java.util.List;

public interface MasterService {

    Master findByGroupNameAndCode(String groupName, String code);
}
