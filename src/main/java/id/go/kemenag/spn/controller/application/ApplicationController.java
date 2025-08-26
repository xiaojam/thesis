package id.go.kemenag.spn.controller.application;

import id.go.kemenag.spn.dto.application.request.ApplicationCreateRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationResponse;
import id.go.kemenag.spn.service.ApplicationService;
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

    final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping(value = "/marriage", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@apiKeyChecker.isValid()")
    @Operation(summary = "create", description = "create a new marriage application")
    ApplicationResponse createMarriage(@RequestBody @Valid ApplicationCreateRequest request) {
        return this.applicationService.createMarriage(request);
    }

    @PostMapping(value = "/divorce", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('DEFAULT')")
    @Operation(summary = "create", description = "create a new divorce application")
    ApplicationResponse createDivorce(@RequestBody @Valid ApplicationCreateRequest request) {
        return this.applicationService.createMarriage(request);
    }
}
