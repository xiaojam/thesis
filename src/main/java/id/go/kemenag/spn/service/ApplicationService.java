package id.go.kemenag.spn.service;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.entity.Application;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {

    List<Application> findAllByStatusInAndType(
        List<ApplicationConstant.Status> statusList,
        ApplicationConstant.Type type
    );

    Application save(Application application);

    List<UUID> collectIds(List<Application> applications);

    void dropExpiredApplications();

    Application findById(UUID id);

    List<Application> findAllABasedOnHandler(
        List<ApplicationConstant.Status> statusList,
        ApplicationConstant.Type type,
        AuthConstant.Role handlerRole,
        String handlerWorkplaceCode
    );

    Application findByIdBasedOnHandler(
        UUID id,
        AuthConstant.Role handlerRole,
        String handlerWorkplaceCode
    );

    Boolean findByBrideAndGroomIdentityId(String identityId, String identityId1, ApplicationConstant.Type type);
}
