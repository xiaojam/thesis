package id.go.kemenag.spn.controller.divorce;

import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.dto.application.request.ApplicationDivorceDoneRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationDivorceDoneResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationDivorceResponse;
import id.go.kemenag.spn.service.divorce.ApplicationDivorceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/divorce")
@Tag(name = "Divorce", description = "Divorce API")
public class DivorceController {

    final ApplicationDivorceService applicationDivorceService;

    @Autowired
    public DivorceController(ApplicationDivorceService applicationDivorceService) {
        this.applicationDivorceService = applicationDivorceService;
    }

    @GetMapping("/application")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(AuthConstant.ROLE_ADMINISTRATOR)
    @Operation(summary = "get all", description = "get all application")
    List<ApplicationDivorceResponse> findAllApplication() {
        return this.applicationDivorceService.findAllApplicationBasedOnHandler();
    }

    @GetMapping("/{applicationId}/application")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(AuthConstant.ROLE_ADMINISTRATOR)
    @Operation(summary = "get", description = "get application by Id")
    ApplicationDivorceResponse findApplicationById(@PathVariable UUID applicationId) {
        return this.applicationDivorceService.findApplicationByIdBasedOnHandler(applicationId);
    }

    @PutMapping("/application/done")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(AuthConstant.ROLE_ADMINISTRATOR)
    @Operation(summary = "done", description = "done application progress")
    ApplicationDivorceDoneResponse doneApplication(@Valid @RequestBody ApplicationDivorceDoneRequest request) {
        return this.applicationDivorceService.doneApplication(request);
    }

}
