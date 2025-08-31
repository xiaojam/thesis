package id.go.kemenag.spn.controller.marriage;

import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageResponse;
import id.go.kemenag.spn.service.marriage.ApplicationMarriageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        return applicationService.findAllApplication();
    }
}
