package id.go.kemenag.spn.activity;

import id.go.kemenag.spn.constant.WorkflowConstant;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class MainActivity implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        UUID applicationId = (UUID) delegateExecution.getVariable(WorkflowConstant.APPLICATION_ID_VARIABLE);
        log.info("Main Activity for ID: {}", applicationId);
    }
}
