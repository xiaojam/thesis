package id.go.kemenag.spn.service;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.MarriageConstant;
import id.go.kemenag.spn.dto.camunda.request.CamundaCompleteUserTaskRequest;
import id.go.kemenag.spn.entity.ApplicationHandler;
import id.go.kemenag.spn.entity.divorce.DivorceCase;
import id.go.kemenag.spn.entity.marriage.Marriage;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.UUID;

public interface CamundaService {

    UUID invokeMarriageProcess(boolean cancelled, Marriage marriage, boolean isMuslim);

    UUID invokeDivorceProcess(boolean cancelled, DivorceCase divorceCase, boolean isMuslim);

    void setVariable(String processInstanceId, String variableName, Object value);

    void completeUserTask(CamundaCompleteUserTaskRequest request, ApplicationHandler handler);
}
