package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.config.property.ApplicationFeatureConfigProperty;
import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.MarriageConstant;
import id.go.kemenag.spn.constant.WorkflowConstant;
import id.go.kemenag.spn.dto.camunda.request.CamundaCompleteUserTaskRequest;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.ApplicationHandler;
import id.go.kemenag.spn.entity.marriage.Marriage;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.service.CamundaService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CamundaServiceImpl implements CamundaService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ApplicationFeatureConfigProperty featureConfig;

    @Override
    public UUID invokeProcess(
        boolean cancelled,
        Marriage marriage,
        boolean isMuslim
    ) {
        var application = marriage.getApplication();
        var applicationId = application.getId();
        var type = application.getType();

        Map<String, Object> variables = new HashMap<>();
        variables.put(WorkflowConstant.APPLICATION_ID_VARIABLE, application.getId());
        variables.put(WorkflowConstant.APPLICATION_TYPE_VARIABLE, type.name());
        variables.put(
            WorkflowConstant.APPLICATION_STATUS_VARIABLE,
            cancelled ? ApplicationConstant.Status.CANCELLED : ApplicationConstant.Status.CREATED.toString()
        );

        if (type.equals(ApplicationConstant.Type.MARRIAGE)) {

            var bride = marriage.getBride();
            var brideDistrictCode = bride.getDistrictCode();
            var brideSubDistrictCode = bride.getSubDistrictCode();
            var groom = marriage.getGroom();
            var groomDistrictCode = groom.getDistrictCode();
            var groomSubDistrictCode = groom.getSubDistrictCode();

            variables.put(WorkflowConstant.BRIDE_DISTRICT_CODE_VARIABLE, brideDistrictCode);
            variables.put(WorkflowConstant.GROOM_DISTRICT_CODE_VARIABLE, groomDistrictCode);
            variables.put(WorkflowConstant.BRIDE_SUB_DISTRICT_CODE_VARIABLE, brideSubDistrictCode);
            variables.put(WorkflowConstant.GROOM_SUB_DISTRICT_CODE_VARIABLE, groomSubDistrictCode);
            variables.put(WorkflowConstant.SAME_VILLAGE_VARIABLE, brideDistrictCode.equals(groomDistrictCode));
            variables.put(WorkflowConstant.MUSLIM_VARIABLE, isMuslim);
        }

        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(
            WorkflowConstant.PROCESS_MAIN_KEY,
            String.valueOf(applicationId),
            variables
        );

        return UUID.fromString(processInstance.getProcessInstanceId());
    }

    @Override
    public void setVariable(String processInstanceId, String variableName, Object value) {
        this.runtimeService.setVariable(processInstanceId, variableName, value);
    }

    @Override
    public void completeUserTask(CamundaCompleteUserTaskRequest request, ApplicationHandler handler) {
        UUID applicationId = handler.getApplication().getId();

        List<Task> tasks = taskService.createTaskQuery()
            .processInstanceId(request.getProcessInstanceId())
            .taskDefinitionKeyIn(request.getTaskNames().toArray(new String[0]))
            .list();

        log.info("Get tasks: {}", tasks);
        if (tasks.isEmpty() && featureConfig.isFeatAllowSubProcess()) {
            String subProcessInstanceId = this.getSubProcessInstanceId(request.getProcessInstanceId());

            if (subProcessInstanceId != null) {
                tasks = taskService.createTaskQuery()
                    .processInstanceId(subProcessInstanceId)
                    .taskDefinitionKeyIn(request.getTaskNames().toArray(new String[0]))
                    .list();
            }
        }

        if (tasks.isEmpty()) {
            throw new BusinessErrorException(
                HttpStatus.NOT_FOUND,
                String.format(
                    "No task found for applicationId=%s, processInstanceId=%s, taskNames=%s",
                    applicationId,
                    request.getProcessInstanceId(),
                    request.getTaskNames()
                )
            );
        }

        for (Task task : tasks) {
            Map<String, Object> inputForm = new HashMap<>();
            if (request.getResultMap() != null) {
                inputForm.putAll(request.getResultMap());
            }

            if (task.getAssignee() == null) {
                taskService.claim(task.getId(), handler.getUsername());
            }

            taskService.complete(task.getId(), inputForm);

            log.info(
                "Task completed: id={}, key={}, name={}, processInstanceId={}, applicationId={}",
                task.getId(),
                task.getTaskDefinitionKey(),
                task.getName(),
                task.getProcessInstanceId(),
                applicationId
            );
        }
    }

    String getSubProcessInstanceId(String processInstanceId) {
        ProcessInstance processInstance =
            this.runtimeService.createProcessInstanceQuery()
                .superProcessInstanceId(processInstanceId)
                .active()
                .singleResult();

        return processInstance != null ? processInstance.getProcessInstanceId() : null;
    }
}
