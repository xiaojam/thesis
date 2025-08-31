package id.go.kemenag.spn.service.marriage;

import id.go.kemenag.spn.dto.application.request.ApplicationMarriageCreateRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageStatusResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageCreateResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface ApplicationMarriageService {

    ApplicationMarriageCreateResponse createMarriage(ApplicationMarriageCreateRequest request);

    ApplicationMarriageStatusResponse checkMarriageStatus(@Valid ApplicationMarriageRequest request);

    List<ApplicationMarriageResponse> findAllApplication();
}
