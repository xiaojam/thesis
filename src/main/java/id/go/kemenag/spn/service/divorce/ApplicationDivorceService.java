package id.go.kemenag.spn.service.divorce;

import id.go.kemenag.spn.dto.application.request.ApplicationDivorceCreateRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationDivorceDoneRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationCreateResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationDivorceDoneResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationDivorceResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ApplicationDivorceService {
    ApplicationCreateResponse createDivorce(@Valid ApplicationDivorceCreateRequest request);

    List<ApplicationDivorceResponse> findAllApplicationBasedOnHandler();

    ApplicationDivorceResponse findApplicationByIdBasedOnHandler(UUID applicationId);

    ApplicationDivorceDoneResponse doneApplication(@Valid ApplicationDivorceDoneRequest request);
}
