package id.go.kemenag.spn.activity;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.WorkflowConstant;
import id.go.kemenag.spn.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
public class CancelledActivity implements JavaDelegate {

    @Autowired
    private ApplicationService applicationService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        UUID applicationId = (UUID) delegateExecution.getVariable(WorkflowConstant.APPLICATION_ID_VARIABLE);

        log.info("Cancelled Application");

        var application = this.applicationService.findById(applicationId);
        application.setStatus(ApplicationConstant.Status.CANCELLED);
        application.setDeleted(Boolean.TRUE);
        application.setDeletedAt(ZonedDateTime.now());
        this.applicationService.save(application);
    }
}
