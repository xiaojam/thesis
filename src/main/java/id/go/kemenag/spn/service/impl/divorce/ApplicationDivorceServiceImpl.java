package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.constant.*;
import id.go.kemenag.spn.dto.application.request.ApplicationDivorceCreateRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationDivorceDoneRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationCreateResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationDivorceDoneResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationDivorceResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationDivorceStatusResponse;
import id.go.kemenag.spn.dto.camunda.request.CamundaCompleteUserTaskRequest;
import id.go.kemenag.spn.dto.caseschedule.response.CaseScheduleResponse;
import id.go.kemenag.spn.dto.child.response.ChildClaimResponse;
import id.go.kemenag.spn.dto.property.response.PropertyClaimResponse;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.divorce.*;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.mapper.*;
import id.go.kemenag.spn.service.*;
import id.go.kemenag.spn.service.divorce.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationDivorceServiceImpl implements ApplicationDivorceService {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private DefendantMapper defendantMapper;

    @Autowired
    private PlaintiffMapper plaintiffMapper;

    @Autowired
    private DivorceMapper divorceMapper;

    @Autowired
    private ChildMapper childMapper;

    @Autowired
    private MarriageDataMapper marriageDataMapper;

    @Autowired
    private PropertyMapper propertyMapper;

    @Autowired
    private DefendantService defendantService;

    @Autowired
    private PlaintiffService plaintiffService;

    @Autowired
    private MarriageDataService marriageDataService;

    @Autowired
    private DivorceService divorceService;

    @Autowired
    private CamundaService camundaService;

    @Autowired
    private ApplicationHandlerService applicationHandlerService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ChildService childService;

    @Autowired
    private AuthService authService;

    @Autowired
    private CourtService courtService;

    @Autowired
    private CaseScheduleMapper caseScheduleMapper;

    @Autowired
    private CaseScheduleService caseScheduleService;

    @Autowired
    private DocumentService documentService;

    @Override
    public ApplicationCreateResponse createDivorce(ApplicationDivorceCreateRequest request) {
        var appplication = Application
            .builder()
            .type(ApplicationConstant.Type.DIVORCE)
            .status(ApplicationConstant.Status.CREATED)
            .build();

        appplication = this.applicationService.save(appplication);

        var defendant = this.processDefendant(request);
        var plaintiff = this.processPlaintiff(request);
        var marriageData = this.processMarriageData(request);

        var caseType = this.determineCaseType(request);

        var isMuslim = DivorceConstant.Religion.ISLAM.equals(plaintiff.getReligion())
            || DivorceConstant.Religion.ISLAM.equals(defendant.getReligion());

        var court = this.courtService.findCourtByCityCode(plaintiff.getCityCode());

        boolean cancelled = court == null;
        var caseNumber = this.buildCaseNumber(plaintiff.getCityCode());
        var courtCode = court != null ? court.getCode() : null;
        var courtName = court != null ? court.getName() : null;

        var divorceCase = DivorceCase
            .builder()
            .application(appplication)
            .caseType(caseType)
            .caseNumber(caseNumber)
            .courtCode(courtCode)
            .courtName(courtName)
            .defendant(defendant)
            .plaintiff(plaintiff)
            .marriageData(marriageData)
            .reconciliationAttemptDescription(request.getReconciliationAttemptDescription())
            .iddahSupportAmount(request.getIddahSupportAmount())
            .mutahDescription(request.getMutahDescription())
            .maddiyahSupportAmount(request.getMaddiyahSupportAmount())
            .maddiyahDurationInMonths(request.getMaddiyahDurationInMonths())
            .build();

        divorceCase = this.divorceService.save(divorceCase);

        this.processChildClaim(request, divorceCase);
        this.processPropertyClaim(request, divorceCase);
        this.processDivorceReason(request, divorceCase);

        if (!cancelled) {
            cancelled = this.applicationHandlerService.setInitialDivorceHandler(appplication, request);
        }

        if (!cancelled) {
            LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
            boolean isDateConditionMet = marriageData.getMarriageDate().isAfter(sixMonthsAgo);

            if (isDateConditionMet) {
                cancelled = true;
            }
        }

       var processId = this.camundaService.invokeDivorceProcess(cancelled, divorceCase, isMuslim);

        appplication.setProcessId(processId);
        appplication.setApplicationNumber(caseNumber);
        appplication = this.applicationService.save(appplication);

        return ApplicationCreateResponse
            .builder()
            .applicationId(appplication.getId())
            .processId(appplication.getProcessId())
            .status(cancelled ? ApplicationConstant.Status.CANCELLED : ApplicationConstant.Status.CREATED)
            .applicationNumber(appplication.getApplicationNumber())
            .build();
    }

    private String buildCaseNumber(String cityCode) {
        var datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern(FormatterConstant.TIME_FORMAT_15));
        var cityPart = cityCode.replaceAll("[^a-zA-Z0-9]", "");
        return String.format("%s%s", cityPart, datePart);
    }

    @Override
    public List<ApplicationDivorceResponse> findAllApplicationBasedOnHandler() {
        var user = this.authService.getCurrentUser();
        if (user == null) {
            throw new BusinessErrorException(HttpStatus.FORBIDDEN, "You are not authorized to access application");
        }

        var applications = this.applicationService
            .findAllABasedOnHandler(
                List.of(ApplicationConstant.Status.PROCESSED),
                ApplicationConstant.Type.DIVORCE,
                user.getRole(),
                user.getWorkplaceCode()
            );

        if (applications.isEmpty()) {
            return List.of();
        }

        var applicationIds = this.applicationService.collectIds(applications);
        var divorceCases = this.divorceService.findAllByApplicationIds(applicationIds);

        if (divorceCases.isEmpty()) {
            return List.of();
        }

        var divorceCaseMap = divorceCases.stream()
            .collect(Collectors.toMap(
                divorceCase -> divorceCase.getApplication().getId(),
                Function.identity()
            ));

        return applications.stream()
            .map(app -> {
                var divorceCase = divorceCaseMap.get(app.getId());

                if (divorceCase == null) {
                    return null;
                }

                return buildApplicationDivorceResponse(app, divorceCase);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public ApplicationDivorceResponse findApplicationByIdBasedOnHandler(UUID applicationId) {
        var user = this.authService.getCurrentUser();
        if (user == null) {
            throw new BusinessErrorException(HttpStatus.FORBIDDEN, "You are not authorized to access application");
        }

        var application = this.applicationService.findByIdBasedOnHandler(
            applicationId,
            user.getRole(),
            user.getWorkplaceCode()
        );

        if (application == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Application not found");
        }

        var divorceCases = this.divorceService.findByApplicationId(applicationId);
        if (divorceCases == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Divorce case not found");
        }

        return buildApplicationDivorceResponse(application, divorceCases);
    }

    @Override
    public ApplicationDivorceDoneResponse doneApplication(ApplicationDivorceDoneRequest request) {
        DivorceCase divorceCase = this.divorceService.findByApplicationId(request.getApplicationId());
        if (divorceCase == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Divorce case not found");
        }

        var handler = this.applicationHandlerService.validateHandler(request.getApplicationId());

        CaseSchedule scheduleToComplete = this.caseScheduleService.findTopScheduledCase(
            request.getApplicationId(),
            request.getDateType(),
            DivorceConstant.ScheduleStatus.SCHEDULED
        );
        
        if (scheduleToComplete == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "No scheduled task found for the specified date type.");
        }

        Map<String, Object> resultMap = new HashMap<>();
        List<String> taskNames = new ArrayList<>(
            List.of(
                ActivityIdConstant.ACTIVITY_SET_COUNCIL_RESULT,
                ActivityIdConstant.ACTIVITY_SET_RECONCILIATION_RESULT,
                ActivityIdConstant.ACTIVITY_DONE_DEFENDANT_S_RESPONSE,
                ActivityIdConstant.ACTIVITY_DONE_PLAINTIFF_S_REPLY,
                ActivityIdConstant.ACTIVITY_DONE_DEFENDANT_S_REJOINDER,
                ActivityIdConstant.ACTIVITY_DONE_PLAINTIFF_S_EVIDENCE,
                ActivityIdConstant.ACTIVITY_DONE_DEFENDANT_S_EVIDENCE,
                ActivityIdConstant.ACTIVITY_DONE_FULL_CLOSING_STATEMENTS,
                ActivityIdConstant.ACTIVITY_DONE_FULL_VERDICT,
                ActivityIdConstant.ACTIVITY_DONE_PLAINTIFF_S_CASE_AND_EVIDENCE,
                ActivityIdConstant.ACTIVITY_DONE_HALF_CLOSING_STATEMENTS,
                ActivityIdConstant.ACTIVITY_DONE_HALF_VERDICT
            )
        );

        switch (request.getDateType()) {
            case COUNCIL_DATE:
                long previousCouncilCount = divorceCase.getSchedules()
                    .stream()
                    .filter(s -> s.getDateType() == DivorceConstant.SetDateType.COUNCIL_DATE)
                    .count();

                DivorceConstant.CouncilResult councilResult = this.getCouncilResult(request, previousCouncilCount);
                resultMap.put(WorkflowConstant.COUNCIL_RESULT_VARIABLE, councilResult.name());
                break;

            case RECONCILIATION_DATE:
                if (request.getIsReconciliationSuccess() == null) {
                    throw new BusinessErrorException(HttpStatus.BAD_REQUEST, "is_reconciliation_success is required for this task.");
                }
                resultMap.put(WorkflowConstant.RECONCILIATION_SUCCESS_VARIABLE, request.getIsReconciliationSuccess());
                break;

            default:
                resultMap.put(WorkflowConstant.COUNCIL_DROPPED_VARIABLE, request.getIsDropped());
                break;
        }

        camundaService.completeUserTask(
            CamundaCompleteUserTaskRequest
                .builder()
                .processInstanceId(String.valueOf(divorceCase.getApplication().getProcessId()))
                .taskNames(taskNames)
                .resultMap(resultMap)
                .build(),
            handler
        );

        scheduleToComplete.setStatus(DivorceConstant.ScheduleStatus.COMPLETED);
        caseScheduleService.save(scheduleToComplete);

        return ApplicationDivorceDoneResponse
            .builder()
            .applicationId(request.getApplicationId())
            .build();
    }

    @Override
    public byte[] downloadDivorceDocument(String applicationNumber) {
        var divorceCase = this.divorceService.findByCaseNumber(applicationNumber);
        if (divorceCase == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Divorce case not found");
        }

        return this.documentService.downloadDivorceDocument(divorceCase);
    }

    @Override
    public ApplicationDivorceStatusResponse checkDivorceStatus(String applicationNumber) {
        var divorceCase = this.divorceService.findByCaseNumber(applicationNumber);
        if (divorceCase == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Divorce case not found");
        }

        var schedules = divorceCase.getSchedules();
        var lastSchedule = schedules.stream()
            .max(Comparator.comparing(CaseSchedule::getProcessStep))
            .orElse(null);

        if (lastSchedule == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "No schedule found for the divorce case.");
        }

        return ApplicationDivorceStatusResponse
            .builder()
            .status(lastSchedule.getStatus())
            .dateType(lastSchedule.getDateType())
            .eventDate(lastSchedule.getEventDate())
            .processStep(lastSchedule.getProcessStep())
            .dailyQueueNumber(lastSchedule.getDailyQueueNumber())
            .build();
    }

    private DivorceConstant.@NotNull CouncilResult getCouncilResult(ApplicationDivorceDoneRequest request, long previousCouncilCount) {
        DivorceConstant.CouncilResult councilResult;
        if (Boolean.TRUE.equals(request.getIsPlaintiffPresent()) && Boolean.TRUE.equals(request.getIsDefendantPresent())) {
            councilResult = DivorceConstant.CouncilResult.FULL;
        } else if (Boolean.TRUE.equals(request.getIsPlaintiffPresent()) || Boolean.TRUE.equals(request.getIsDefendantPresent())) {
            councilResult = DivorceConstant.CouncilResult.HALF;
        } else {
            councilResult = (previousCouncilCount >= 3) ? DivorceConstant.CouncilResult.DROP : DivorceConstant.CouncilResult.FAIL;
        }
        return councilResult;
    }

    private Defendant processDefendant(ApplicationDivorceCreateRequest request) {
        var defendant = this.defendantMapper.convert(request.getDefendant());
        return this.defendantService.save(defendant);
    }

    private Plaintiff processPlaintiff(ApplicationDivorceCreateRequest request) {
        var plaintiff = this.plaintiffMapper.convert(request.getPlaintiff());
        return this.plaintiffService.save(plaintiff);
    }

    private MarriageData processMarriageData(ApplicationDivorceCreateRequest request) {
        var marriageData = this.marriageDataMapper.convert(request.getMarriageData());
        return this.marriageDataService.save(marriageData);
    }

    private void processChildClaim(ApplicationDivorceCreateRequest request, DivorceCase divorceCase) {
        if (request.getChildClaim() == null) {
            return;
        }

        var childClaim = ChildClaim
            .builder()
            .divorceCase(divorceCase)
            .claimed(request.getChildClaim().getClaimed())
            .custodyRequest(request.getChildClaim().getCustodyRequest())
            .monthlySupport(request.getChildClaim().getMonthlySupport())
            .children(new ArrayList<>())
            .build();

        List<Child> children = request.getChildClaim().getChildren().stream()
            .map(childRequest -> this.childMapper.convert(childRequest))
            .map(child -> this.childService.save(child))
            .collect(Collectors.toList());

        childClaim.setChildren(children);

        this.childService.save(childClaim);
    }

    private void processPropertyClaim(ApplicationDivorceCreateRequest request, DivorceCase divorceCase) {
        if (request.getPropertyClaim() == null) {
            return;
        }

        var propertyClaim = PropertyClaim
            .builder()
            .divorceCase(divorceCase)
            .divisionRequest(request.getPropertyClaim().getDivisionRequest())
            .properties(new ArrayList<>())
            .build();

        propertyClaim = this.propertyService.save(propertyClaim);

        for (var propertyRequest : request.getPropertyClaim().getProperties()) {
            var property = this.propertyMapper.convert(propertyRequest);
            property.setPropertyClaim(propertyClaim);
            this.propertyService.save(property);
        }
    }

    private void processDivorceReason(ApplicationDivorceCreateRequest request, DivorceCase divorceCase) {
        if (request.getDivorceReason() == null) {
            return;
        }

        var divorceReason = this.divorceMapper.convert(request.getDivorceReason());
        divorceReason.setDivorceCase(divorceCase);

        this.divorceService.save(divorceReason);
    }

    private DivorceConstant.CaseType determineCaseType(ApplicationDivorceCreateRequest request) {
        boolean hasPropertyClaim = request.getPropertyClaim() != null
            && request.getPropertyClaim().getProperties() != null
            && !request.getPropertyClaim().getProperties().isEmpty();

        boolean hasChildClaim = request.getChildClaim() != null
            && request.getChildClaim().getChildren() != null
            && !request.getChildClaim().getChildren().isEmpty();

        if (hasPropertyClaim && hasChildClaim) {
            return DivorceConstant.CaseType.COMPLETE;
        } else if (hasPropertyClaim) {
            return DivorceConstant.CaseType.PROPERTY;
        } else if (hasChildClaim) {
            return DivorceConstant.CaseType.CHILD_CUSTODY;
        } else {
            return DivorceConstant.CaseType.BASIC;
        }
    }

    private ApplicationDivorceResponse buildApplicationDivorceResponse(Application application, DivorceCase divorceCase) {
        var plaintiff = this.plaintiffMapper.convert(divorceCase.getPlaintiff());
        var defendant = this.defendantMapper.convert(divorceCase.getDefendant());
        var marriageData = this.marriageDataMapper.convert(divorceCase.getMarriageData());
        var divorceReason = divorceCase.getDivorceReason() != null
            ? this.divorceMapper.convert(divorceCase.getDivorceReason())
            : null;

        var childClaim = divorceCase.getChildClaim() != null
            ? ChildClaimResponse
                .builder()
                .custodyRequest(divorceCase.getChildClaim().getCustodyRequest())
                .monthlySupport(divorceCase.getChildClaim().getMonthlySupport())
                .children(
                    divorceCase.getChildClaim().getChildren().stream()
                        .map(child -> this.childMapper.convert(child))
                        .collect(Collectors.toList())
                )
                .build()
            : null;

        var propertyClaim = divorceCase.getPropertyClaim() != null
            ? PropertyClaimResponse
                .builder()
                .divisionRequest(divorceCase.getPropertyClaim().getDivisionRequest())
                .properties(
                    divorceCase.getPropertyClaim().getProperties().stream()
                        .map(property -> this.propertyMapper.convert(property))
                        .collect(Collectors.toList())
                )
                .build()
            : null;

        var schedules = divorceCase.getSchedules()
            .stream()
            .map(this.caseScheduleMapper::convert)
            .sorted(Comparator.comparing(CaseScheduleResponse::getProcessStep))
            .toList();

        return ApplicationDivorceResponse
            .builder()
            .applicationId(application.getId())
            .processId(application.getProcessId())
            .status(application.getStatus())
            .caseNumber(divorceCase.getCaseNumber())
            .courtCode(divorceCase.getCourtCode())
            .courtName(divorceCase.getCourtName())
            .plaintiff(plaintiff)
            .defendant(defendant)
            .marriageData(marriageData)
            .divorceReason(divorceReason)
            .childClaim(childClaim)
            .propertyClaim(propertyClaim)
            .schedules(schedules)
            .reconciliationAttemptDescription(divorceCase.getReconciliationAttemptDescription())
            .iddahSupportAmount(divorceCase.getIddahSupportAmount())
            .mutahDescription(divorceCase.getMutahDescription())
            .maddiyahSupportAmount(divorceCase.getMaddiyahSupportAmount())
            .maddiyahDurationInMonths(divorceCase.getMaddiyahDurationInMonths())
            .build();
    }
}
