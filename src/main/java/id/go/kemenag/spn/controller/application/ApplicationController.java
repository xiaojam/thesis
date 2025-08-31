package id.go.kemenag.spn.controller.application;

import id.go.kemenag.spn.dto.application.request.ApplicationMarriageCreateRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageStatusResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageCreateResponse;
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

    final ApplicationMarriageService applicationService;

    @Autowired
    public ApplicationController(ApplicationMarriageService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping(value = "/marriage", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@apiKeyChecker.isValid()")
    @Operation(summary = "create", description = "create a new marriage application")
    ApplicationMarriageCreateResponse createMarriage(@RequestBody @Valid ApplicationMarriageCreateRequest request) {
        return this.applicationService.createMarriage(request);
    }

    @PostMapping(value = "/divorce", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@apiKeyChecker.isValid()")
    @Operation(summary = "create", description = "create a new divorce application")
    ApplicationMarriageCreateResponse createDivorce(@RequestBody @Valid ApplicationMarriageCreateRequest request) {
        return this.applicationService.createMarriage(request);
    }

    @GetMapping(value = "/marriage/status", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@apiKeyChecker.isValid()")
    @Operation(summary = "get", description = "get application status by id")
    ApplicationMarriageStatusResponse checkMarriageStatus(@RequestBody @Valid ApplicationMarriageRequest request) {
        return this.applicationService.checkMarriageStatus(request);
    }
}
