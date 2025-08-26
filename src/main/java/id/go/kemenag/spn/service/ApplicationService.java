package id.go.kemenag.spn.service;

import id.go.kemenag.spn.constant.WorkflowConstant;
import id.go.kemenag.spn.dto.application.request.ApplicationCreateRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationResponse;
import id.go.kemenag.spn.entity.Application;

import java.util.UUID;

public interface ApplicationService {

    ApplicationResponse createMarriage(ApplicationCreateRequest request);
}
