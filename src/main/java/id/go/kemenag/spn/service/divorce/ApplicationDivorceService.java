package id.go.kemenag.spn.service.divorce;

import id.go.kemenag.spn.dto.application.request.ApplicationDivorceCreateRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationCreateResponse;
import jakarta.validation.Valid;

public interface ApplicationDivorceService {
    ApplicationCreateResponse createDivorce(@Valid ApplicationDivorceCreateRequest request);
}
