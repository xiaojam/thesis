package id.go.kemenag.spn.service;

import id.go.kemenag.spn.dto.application.request.ApplicationMarriageCreateRequest;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.ApplicationHandler;

import java.util.List;
import java.util.UUID;

public interface ApplicationHandlerService {

    ApplicationHandler save(ApplicationHandler applicationHandler);

    /**
     *
     * @return TRUE if location type isn't OTHER
     */
    Boolean setInitialHandler(Application application, ApplicationMarriageCreateRequest request);

    ApplicationHandler  validateHandler(UUID applicationId);

    ApplicationHandler getLastHandler(List<ApplicationHandler> handlers);
}
