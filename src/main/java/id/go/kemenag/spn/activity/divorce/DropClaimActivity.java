package id.go.kemenag.spn.activity.divorce;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.WorkflowConstant;
import id.go.kemenag.spn.service.divorce.DivorceService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
public class DropClaimActivity implements JavaDelegate {

    @Autowired
    private DivorceService divorceService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        UUID applicationId = (UUID) delegateExecution.getVariable(WorkflowConstant.APPLICATION_ID_VARIABLE);

        log.info("Drop Claim");

        var divorceCase = this.divorceService.findByApplicationId(applicationId);
        if (divorceCase != null) {
            var application = divorceCase.getApplication();
            application.setStatus(ApplicationConstant.Status.CANCELLED);
            application.setDeleted(Boolean.TRUE);
            application.setDeletedAt(ZonedDateTime.now());

            this.divorceService.save(divorceCase);

        }
    }
}
