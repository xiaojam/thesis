package id.go.kemenag.spn.service.marriage;

import id.go.kemenag.spn.dto.application.request.*;
import id.go.kemenag.spn.dto.application.response.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ApplicationMarriageService {

    ApplicationCreateResponse createMarriage(ApplicationMarriageCreateRequest request);

    ApplicationMarriageStatusResponse checkMarriageStatus(ApplicationMarriageRequest request);

    List<ApplicationMarriageResponse> findAllApplication();

    ApplicationMarriageResponse findApplicationById(UUID applicationId);

    List<ApplicationMarriageResponse> findAllApplicationBasedOnHandler();

    ApplicationMarriageResponse findApplicationByIdBasedOnHandler(UUID applicationId);

    ApplicationMarriageApproveResponse approveApplication(ApplicationMarriageApproveRequest request);

    ApplicationMarriageUpdateResponse updateApplicationById(UUID applicationId, ApplicationMarriageUpdateRequest request);

    byte[] downloadMarriageDocument(UUID applicationId);
}
