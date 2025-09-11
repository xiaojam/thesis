package id.go.kemenag.spn.controller.marriage;

import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageApproveRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageUpdateRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageApproveResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageUpdateResponse;
import id.go.kemenag.spn.service.marriage.ApplicationMarriageService;
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
@RequestMapping(value = "/v1/marriage")
@Tag(name = "Marriage", description = "Marriage API")
public class MarriageController {

    final ApplicationMarriageService applicationService;

    @Autowired
    public MarriageController(ApplicationMarriageService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/application")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(AuthConstant.ROLE_MARRIAGE_PROCESSOR)
    @Operation(summary = "get all", description = "get all application")
    List<ApplicationMarriageResponse> findAllApplication() {
        return applicationService.findAllApplicationBasedOnHandler();
    }

    @GetMapping("/{applicationId}/application")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(AuthConstant.ROLE_MARRIAGE_PROCESSOR)
    @Operation(summary = "get", description = "get application by Id")
    ApplicationMarriageResponse findApplicationById(@PathVariable UUID applicationId) {
        return this.applicationService.findApplicationByIdBasedOnHandler(applicationId);
    }

    @PutMapping("/{applicationId}/application")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(AuthConstant.ROLE_MARRIAGE_PROCESSOR)
    @Operation(summary = "update", description = "update application by id")
    ApplicationMarriageUpdateResponse updateApplicationById(
        @PathVariable UUID applicationId,
        @RequestBody @Valid ApplicationMarriageUpdateRequest request
    ) {
        return this.applicationService.updateApplicationById(applicationId, request);
    }

    @PutMapping("/application/approve")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(AuthConstant.ROLE_MARRIAGE_PROCESSOR)
    @Operation(summary = "update", description = "approve application")
    ApplicationMarriageApproveResponse approveApplication(@RequestBody @Valid ApplicationMarriageApproveRequest request) {
        return this.applicationService.approveApplication(request);
    }
}
