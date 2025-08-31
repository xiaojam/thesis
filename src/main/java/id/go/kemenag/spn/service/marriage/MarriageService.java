package id.go.kemenag.spn.service.marriage;

import id.go.kemenag.spn.entity.marriage.Marriage;

import java.util.List;
import java.util.UUID;

public interface MarriageService {

    Marriage save(Marriage marriage);

    List<Marriage> findAllByApplicationIds(List<UUID> applicationIds);
}
