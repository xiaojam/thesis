package id.go.kemenag.spn.activity.marriage;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.constant.MarriageConstant;
import id.go.kemenag.spn.constant.WorkflowConstant;
import id.go.kemenag.spn.entity.ApplicationHandler;
import id.go.kemenag.spn.entity.marriage.Marriage;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.service.ApplicationHandlerService;
import id.go.kemenag.spn.service.UserService;
import id.go.kemenag.spn.service.marriage.MarriageService;
import id.go.kemenag.spn.service.master.MasterService;
import id.go.kemenag.spn.util.MasterUtil;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@Component
@Slf4j
public class SetHandlerActivity implements JavaDelegate {

    @Autowired
    private MarriageService marriageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationHandlerService applicationHandlerService;

    @Autowired
    private MasterService masterService;

    @Override
    public void execute(DelegateExecution execution) {
        UUID applicationId = (UUID) execution.getVariable(WorkflowConstant.APPLICATION_ID_VARIABLE);
        log.info("Starting SetHandlerActivity for Application ID: {}", applicationId);

        var marriage = this.marriageService.findByApplicationId(applicationId);

        if (marriage != null) {
            this.processNewHandler(execution, marriage);
        } else {
            log.error("Marriage data not found for Application ID: {}", applicationId);
        }
    }

    private void processNewHandler(DelegateExecution execution, Marriage marriage) {
        AuthConstant.Role newRole = getRequiredRole(execution);
        log.info("New handler role to be set: {}", newRole);

        String workplaceCode = determineWorkplaceCode(execution, marriage.getLocationType());
        log.info("Determined new workplace code: {}", workplaceCode);

        var user = this.userService.findByWorkplaceCodeAndRole(workplaceCode, newRole);
        var application = marriage.getApplication();

        ApplicationHandler newHandler = ApplicationHandler.builder()
            .role(newRole)
            .workplaceCode(workplaceCode)
            .application(application)
            .username(user != null ? user.getUsername() : null)
            .build();

        var savedHandler = applicationHandlerService.save(newHandler);
        log.info("Successfully saved new handler -> Username: {}, Role: {}", savedHandler.getUsername(), savedHandler.getRole());
    }

    private String determineWorkplaceCode(DelegateExecution execution, MarriageConstant.LocationType locationType) {
        boolean isSecondProcess = execution.getVariable(WorkflowConstant.SET_SECOND_PROCESS_VARIABLE) != null;
        ApplicationConstant.WorkplaceType workplaceType = getWorkplaceType(execution);

        boolean isBrideLocationPrimary = List.of(
            MarriageConstant.LocationType.BRIDE_HOME,
            MarriageConstant.LocationType.BRIDE_KUA
        ).contains(locationType);
        log.info("Is bride's location the primary ceremony location? {}", isBrideLocationPrimary);

        String brideDistrictCode = (String) execution.getVariable(WorkflowConstant.BRIDE_DISTRICT_CODE_VARIABLE);
        String brideSubDistrictCode = (String) execution.getVariable(WorkflowConstant.BRIDE_SUB_DISTRICT_CODE_VARIABLE);
        String groomDistrictCode = (String) execution.getVariable(WorkflowConstant.GROOM_DISTRICT_CODE_VARIABLE);
        String groomSubDistrictCode = (String) execution.getVariable(WorkflowConstant.GROOM_SUB_DISTRICT_CODE_VARIABLE);

        String primaryLocationCode;
        String secondaryLocationCode;
        if (workplaceType == ApplicationConstant.WorkplaceType.VILLAGE) {
            primaryLocationCode = isBrideLocationPrimary ? brideSubDistrictCode : groomSubDistrictCode;
            secondaryLocationCode = isBrideLocationPrimary ? groomSubDistrictCode : brideSubDistrictCode;
        } else {
            var brideKUADistrictCode = this.getKUADistrictCode(brideDistrictCode);
            var groomKUADistrictCode = this.getKUADistrictCode(groomDistrictCode);

            primaryLocationCode = isBrideLocationPrimary ? brideKUADistrictCode : groomKUADistrictCode;
            secondaryLocationCode = isBrideLocationPrimary ? groomKUADistrictCode : brideKUADistrictCode;
        }

        if (isSecondProcess) {
            execution.setVariable(WorkflowConstant.SET_SECOND_PROCESS_VARIABLE, true);
            return primaryLocationCode;
        } else {
            return secondaryLocationCode;
        }
    }

    private String getKUADistrictCode(String districtCode) {
        var data = this.masterService.findByGroupNameAndCode("WILAYAH", districtCode);
        var kua = MasterUtil.getKUA(data.getChildren());
        return kua.getCode();
    }

    private AuthConstant.Role getRequiredRole(DelegateExecution execution) {
        String roleName = (String) execution.getVariable(WorkflowConstant.SET_HANDLER_ROLE_VARIABLE);
        return AuthConstant.Role.fromString(roleName)
            .orElseThrow(() ->
                new BusinessErrorException(HttpStatus.NOT_FOUND, "Role not found in process variable: " + roleName)
            );
    }

    private ApplicationConstant.WorkplaceType getWorkplaceType(DelegateExecution execution) {
        String typeName = (String) execution.getVariable(WorkflowConstant.SET_WORKPLACE_TYPE_VARIABLE);
        return ApplicationConstant.WorkplaceType.fromString(typeName)
            .orElseThrow(() ->
                new BusinessErrorException(HttpStatus.NOT_FOUND, "Workplace type not found in process variable: " + typeName)
            );
    }
}
