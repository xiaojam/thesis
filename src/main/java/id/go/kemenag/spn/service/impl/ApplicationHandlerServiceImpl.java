package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.config.custom.CustomUserDetails;
import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.constant.MarriageConstant;
import id.go.kemenag.spn.dto.application.request.ApplicationDivorceCreateRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageCreateRequest;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.ApplicationHandler;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.repository.ApplicationHandlerRepository;
import id.go.kemenag.spn.service.ApplicationHandlerService;
import id.go.kemenag.spn.service.AuthService;
import id.go.kemenag.spn.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ApplicationHandlerServiceImpl implements ApplicationHandlerService {

    @Autowired
    private ApplicationHandlerRepository applicationHandlerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Override
    public ApplicationHandler save(ApplicationHandler applicationHandler) {
        return this.applicationHandlerRepository.save(applicationHandler);
    }

    @Override
    public Boolean setInitialMarriageHandler(Application application, ApplicationMarriageCreateRequest request) {
        var locationType = request.getMarriage().getLocationType();
        if (List.of(MarriageConstant.LocationType.BRIDE_HOME, MarriageConstant.LocationType.BRIDE_KUA).contains(locationType)) {
            return this.buildInitialHandler(application, request.getGroom().getSubDistrictCode());
        }

        if (List.of(MarriageConstant.LocationType.GROOM_HOME, MarriageConstant.LocationType.GROOM_KUA).contains(locationType)) {
            return this.buildInitialHandler(application, request.getBride().getSubDistrictCode());
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean setInitialDivorceHandler(Application application, ApplicationDivorceCreateRequest request) {
        return null;
    }

    public Boolean buildInitialHandler(Application application, String subDistrictCode) {
        log.info("Workplace Code: {}", subDistrictCode);
        var user = this.userService.findByWorkplaceCodeAndRole(subDistrictCode, AuthConstant.Role.REGISTRAR);

        var handler = ApplicationHandler
            .builder()
            .role(AuthConstant.Role.REGISTRAR)
            .username(user.getUsername())
            .workplaceCode(subDistrictCode)
            .application(application)
            .build();

        this.applicationHandlerRepository.save(handler);

        return Boolean.FALSE;
    }

    private List<ApplicationHandler> findByApplicationId(UUID applicationId) {
        return Streamable
            .of(this.applicationHandlerRepository.findAllByApplicationIdAndDeletedFalse(applicationId))
            .stream()
            .toList();
    }

    @Override
    public ApplicationHandler validateHandler(UUID applicationId) {
        var user = this.authService.getCurrentUser();
        var handlers = this.findByApplicationId(applicationId);

        var lastHandler = getLastHandler(handlers);
        if (lastHandler == null) {
            throw new BusinessErrorException(HttpStatus.FORBIDDEN, "No handler assigned for this application");
        }

        if (lastHandler.getRole() != user.getRole() || !lastHandler.getWorkplaceCode().equals(user.getWorkplaceCode())) {
            throw new BusinessErrorException(HttpStatus.FORBIDDEN, "You are not authorized to update this application");
        }

        return lastHandler;
    }

    @Override
    public ApplicationHandler getLastHandler(List<ApplicationHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            return null;
        }

        return handlers
            .stream()
            .max(Comparator.comparing(ApplicationHandler::getCreatedAt))
            .orElse(null);
    }
}
