package id.go.kemenag.spn.activity;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.WorkflowConstant;
import id.go.kemenag.spn.service.ApplicationService;
import id.go.kemenag.spn.service.CamundaService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class MainActivity implements JavaDelegate {

    @Autowired
    private CamundaService camundaService;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        UUID applicationId = (UUID) delegateExecution.getVariable(WorkflowConstant.APPLICATION_ID_VARIABLE);

        /**
         * Currently by passed
         * Implement trigger by user to process application
         */
        var application = this.applicationService.findById(applicationId);
        if (application != null && !application.getStatus().equals(ApplicationConstant.Status.CANCELLED)) {
            application.setStatus(ApplicationConstant.Status.PROCESSED);
            applicationService.save(application);

            this.camundaService.setVariable(
                delegateExecution.getProcessInstanceId(),
                WorkflowConstant.CANCELLED_APPLICATION_VARIABLE,
                Boolean.FALSE
            );
        } else {
            this.camundaService.setVariable(
                delegateExecution.getProcessInstanceId(),
                WorkflowConstant.CANCELLED_APPLICATION_VARIABLE,
                Boolean.TRUE
            );
        }

    }
}
