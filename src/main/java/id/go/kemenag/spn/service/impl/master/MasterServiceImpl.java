package id.go.kemenag.spn.service.impl.master;

import id.go.kemenag.spn.entity.master.Master;
import id.go.kemenag.spn.repository.master.MasterRepository;
import id.go.kemenag.spn.service.master.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MasterServiceImpl implements MasterService {

    @Autowired
    private MasterRepository masterRepository;

    @Override
    public Master findByGroupNameAndCode(String groupName, String code) {
        return this.masterRepository.findByGroupNameAndCodeAndDeletedFalse(groupName, code).orElse(null);
    }
}
