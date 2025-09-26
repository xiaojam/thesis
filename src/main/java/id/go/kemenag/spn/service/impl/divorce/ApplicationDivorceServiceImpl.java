package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.dto.application.request.ApplicationDivorceCreateRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationCreateResponse;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.divorce.*;
import id.go.kemenag.spn.mapper.*;
import id.go.kemenag.spn.service.ApplicationService;
import id.go.kemenag.spn.service.CamundaService;
import id.go.kemenag.spn.service.divorce.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        var isMuslim = DivorceConstant.Religion.ISLAM.equals(plaintiff.getReligion())
            || DivorceConstant.Religion.ISLAM.equals(defendant.getReligion());

        var divorceCase = DivorceCase
            .builder()
            .application(appplication)
            .defendant(defendant)
            .plaintiff(plaintiff)
            .marriageData(marriageData)
            .build();

        divorceCase = this.divorceService.save(divorceCase);

        this.processChildClaim(request, divorceCase);
        this.processPropertyClaim(request, divorceCase);
        this.processDivorceReason(request, divorceCase);

        boolean cancelled = false;

        var processId = this.camundaService.invokeDivorceProcess(cancelled, divorceCase, isMuslim);

        appplication.setProcessId(processId);
        appplication = this.applicationService.save(appplication);

        return ApplicationCreateResponse
            .builder()
            .applicationId(appplication.getId())
            .processId(appplication.getProcessId())
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

    private ChildClaim processChildClaim(ApplicationDivorceCreateRequest request, DivorceCase divorceCase) {
        if (request.getChildClaim() == null) {
            return null;
        }

        var childClaim = ChildClaim
            .builder()
            .divorceCase(divorceCase)
            .custodyRequest(request.getChildClaim().getCustodyRequest())
            .monthlySupport(request.getChildClaim().getMonthlySupport())
            .build();

        for (var childRequest : request.getChildClaim().getChildren()) {
            var child = this.childMapper.convert(childRequest);
            childClaim.getChildren().add(child);
        }

        return childClaim;
    }

    private PropertyClaim processPropertyClaim(ApplicationDivorceCreateRequest request, DivorceCase divorceCase) {
        if (request.getPropertyClaim() == null) {
            return null;
        }

        var propertyClaim = PropertyClaim
            .builder()
            .divorceCase(divorceCase)
            .divisionRequest(request.getPropertyClaim().getDivisionRequest())
            .build();

        for (var propertyRequest : request.getPropertyClaim().getProperties()) {
            var property = this.propertyMapper.convert(propertyRequest);
            propertyClaim.getProperties().add(property);
        }

        return propertyClaim;
    }

    private DivorceReason processDivorceReason(ApplicationDivorceCreateRequest request, DivorceCase divorceCase) {
        if (request.getDivorceReason() == null) {
            return null;
        }

        var divorceReason = this.divorceMapper.convert(request.getDivorceReason());
        divorceReason.setDivorceCase(divorceCase);
        return divorceReason;
    }
}
