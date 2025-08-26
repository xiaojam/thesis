package id.go.kemenag.spn.service;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.WorkflowConstant;

import java.util.UUID;

public interface CamundaService {

    UUID invokeProcess(ApplicationConstant.Type type, UUID applicationId);
}
