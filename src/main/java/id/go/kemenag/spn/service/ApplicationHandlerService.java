package id.go.kemenag.spn.service;

import id.go.kemenag.spn.dto.application.request.ApplicationDivorceCreateRequest;
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
    Boolean setInitialMarriageHandler(Application application, ApplicationMarriageCreateRequest request);

    /**
     *
     * @return TRUE if location type isn't OTHER
     */
    Boolean setInitialDivorceHandler(Application application, ApplicationDivorceCreateRequest request);


    ApplicationHandler  validateHandler(UUID applicationId);

    ApplicationHandler getLastHandler(List<ApplicationHandler> handlers);
}
