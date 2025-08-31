package id.go.kemenag.spn.service;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.entity.Application;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {

    List<Application> findAllAndStatusInAndType(
        List<ApplicationConstant.Status> statusList,
        ApplicationConstant.Type type
    );

    Application save(Application application);

    List<UUID> collectIds(List<Application> applications);
}
