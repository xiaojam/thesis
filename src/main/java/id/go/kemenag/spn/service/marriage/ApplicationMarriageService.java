package id.go.kemenag.spn.service.marriage;

import id.go.kemenag.spn.dto.application.request.ApplicationMarriageApproveRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageCreateRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageUpdateRequest;
import id.go.kemenag.spn.dto.application.response.*;

import java.util.List;
import java.util.UUID;

public interface ApplicationMarriageService {

    ApplicationMarriageCreateResponse createMarriage(ApplicationMarriageCreateRequest request);

    ApplicationMarriageStatusResponse checkMarriageStatus(ApplicationMarriageRequest request);

    List<ApplicationMarriageResponse> findAllApplication();

    ApplicationMarriageResponse findApplicationById(UUID applicationId);

    List<ApplicationMarriageResponse> findAllApplicationBasedOnHandler();

    ApplicationMarriageResponse findApplicationByIdBasedOnHandler(UUID applicationId);

    ApplicationMarriageApproveResponse approveApplication(ApplicationMarriageApproveRequest request);

    ApplicationMarriageUpdateResponse updateApplicationById(UUID applicationId, ApplicationMarriageUpdateRequest request);
}
