package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.WorkflowConstant;
import id.go.kemenag.spn.service.CamundaService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CamundaServiceImpl implements CamundaService {

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public UUID invokeProcess(ApplicationConstant.Type type, UUID applicationId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(WorkflowConstant.APPLICATION_ID_VARIABLE, applicationId);
        variables.put(WorkflowConstant.APPLICATION_TYPE_VARIABLE, type.toString());
        variables.put(WorkflowConstant.APPLICATION_STATUS_VARIABLE, ApplicationConstant.Status.CREATED.toString());

        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(
            WorkflowConstant.PROCESS_MAIN_KEY,
            variables
        );

        return UUID.fromString(processInstance.getProcessInstanceId());
    }
}
