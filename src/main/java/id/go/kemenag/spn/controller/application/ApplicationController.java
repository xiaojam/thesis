package id.go.kemenag.spn.controller.application;

import id.go.kemenag.spn.dto.application.request.ApplicationDivorceCreateRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageCreateRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageStatusResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationCreateResponse;
import id.go.kemenag.spn.service.divorce.ApplicationDivorceService;
import id.go.kemenag.spn.service.marriage.ApplicationMarriageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/application")
@Tag(name = "Application", description = "Application API")
public class ApplicationController {

    final ApplicationMarriageService applicationMarriageService;

    final ApplicationDivorceService applicationDivorceService;

    @Autowired
    public ApplicationController(
        ApplicationMarriageService applicationMarriageService,
        ApplicationDivorceService applicationDivorceService
    ) {
        this.applicationMarriageService = applicationMarriageService;
        this.applicationDivorceService = applicationDivorceService;
    }

    @PostMapping(value = "/marriage", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@apiKeyChecker.isValid()")
    @Operation(summary = "create", description = "create a new marriage application")
    ApplicationCreateResponse createMarriage(@RequestBody @Valid ApplicationMarriageCreateRequest request) {
        return this.applicationMarriageService.createMarriage(request);
    }

    @PostMapping(value = "/divorce", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@apiKeyChecker.isValid()")
    @Operation(summary = "create", description = "create a new divorce application")
    ApplicationCreateResponse createDivorce(@RequestBody @Valid ApplicationDivorceCreateRequest request) {
        return this.applicationDivorceService.createDivorce(request);
    }

    @GetMapping(value = "/marriage/status", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@apiKeyChecker.isValid()")
    @Operation(summary = "get", description = "get application status by id")
    ApplicationMarriageStatusResponse checkMarriageStatus(@RequestBody @Valid ApplicationMarriageRequest request) {
        return this.applicationMarriageService.checkMarriageStatus(request);
    }
}
