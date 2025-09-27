package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.dto.application.request.ApplicationDivorceCreateRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationCreateResponse;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.divorce.*;
import id.go.kemenag.spn.mapper.*;
import id.go.kemenag.spn.service.ApplicationHandlerService;
import id.go.kemenag.spn.service.ApplicationService;
import id.go.kemenag.spn.service.CamundaService;
import id.go.kemenag.spn.service.divorce.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

        var divorceCase = DivorceCase
            .builder()
            .application(appplication)
            .caseType(caseType)
            .defendant(defendant)
            .plaintiff(plaintiff)
            .marriageData(marriageData)
            .build();

        divorceCase = this.divorceService.save(divorceCase);

        this.processChildClaim(request, divorceCase);
        this.processPropertyClaim(request, divorceCase);
        this.processDivorceReason(request, divorceCase);

        boolean cancelled = this.applicationHandlerService.setInitialDivorceHandler(appplication, request);
        if (!cancelled) {
            LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
            boolean isDateConditionMet = marriageData.getMarriageDate().isAfter(sixMonthsAgo);

            if (isDateConditionMet) {
                cancelled = true;
            }
        }

       var processId = this.camundaService.invokeDivorceProcess(cancelled, divorceCase, isMuslim);

        appplication.setProcessId(processId);
        appplication = this.applicationService.save(appplication);

        return ApplicationCreateResponse
            .builder()
            .applicationId(appplication.getId())
            .processId(appplication.getProcessId())
            .status(cancelled ? ApplicationConstant.Status.CANCELLED : ApplicationConstant.Status.CREATED)
            .build();
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
            .properties(new ArrayList<>()) // <-- PENTING: Inisialisasi list
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
}
