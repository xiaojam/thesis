package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.MarriageConstant;
import id.go.kemenag.spn.dto.application.request.ApplicationCreateRequest;
import id.go.kemenag.spn.dto.application.request.ApplicationMarriageRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationMarriageStatusResponse;
import id.go.kemenag.spn.dto.application.response.ApplicationResponse;
import id.go.kemenag.spn.dto.marriage.request.MarriageCreateRequest;
import id.go.kemenag.spn.dto.previouspartner.request.PreviousPartnerCreateRequest;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.marriage.*;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.mapper.*;
import id.go.kemenag.spn.repository.ApplicationRepository;
import id.go.kemenag.spn.service.*;
import id.go.kemenag.spn.util.ErrorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private BrideService brideService;

    @Autowired
    private BrideFatherMapper brideFatherMapper;

    @Autowired
    private BrideMotherMapper brideMotherMapper;

    @Autowired
    private GroomService groomService;

    @Autowired
    private GroomFatherMapper groomFatherMapper;

    @Autowired
    private GroomMotherMapper groomMotherMapper;

    @Autowired
    private GuardianMapper guardianMapper;

    @Autowired
    private PreviousPartnerMapper previousPartnerMapper;

    @Autowired
    private MarriageMapper marriageMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MarriageService marriageService;

    @Autowired
    private CamundaService camundaService;

    @Override
    public ApplicationResponse createMarriage(ApplicationCreateRequest request) {
        var application = Application
            .builder()
            .type(ApplicationConstant.Type.MARRIAGE)
            .status(ApplicationConstant.Status.CREATED)
            .build();

        application = this.save(application);

        var brideFather = this.processBrideFather(request);
        var brideMother = this.processBrideMother(request);
        var groomFather = this.processGroomFather(request);
        var groomMother = this.processGroomMother(request);
        var guardian = this.processGuardian(request);
        var previousGroomPartner = this.processGroomPreviousPartner(request);
        var previousBridePartner = this.processBridePreviousPartner(request);

        var processId = this.camundaService.invokeProcess(application.getType(), application.getId());
        application.setProcessId(processId);
        application = this.save(application);

        this.processBride(application, request, brideFather, brideMother, guardian, previousBridePartner);
        this.processGroom(application, request, groomFather, groomMother, previousGroomPartner);
        this.processMarriage(request.getMarriage(), application);

        return ApplicationResponse
            .builder()
            .applicationId(application.getId())
            .processId(processId)
            .build();
    }

    @Override
    public ApplicationMarriageStatusResponse checkMarriageStatus(ApplicationMarriageRequest request) {
        var groom = this.groomService.findFirstByIdentityId(request.getGroomIdentityId());
        var bride = this.brideService.findFirstByIdentityId(request.getBrideIdentityId());

        this.validateCoupleData(request, groom, bride);

        return ApplicationMarriageStatusResponse
            .builder()
            .status(groom.getApplication().getStatus().name())
            .groomName(String.format("%s %s", groom.getFirstName(), groom.getLastName()))
            .brideName(String.format("%s %s", bride.getFirstName(), bride.getLastName()))
            .build();
    }

    private void validateCoupleData(ApplicationMarriageRequest request, Groom groom, Bride bride) {
        Map<String, String> errors = new HashMap<>();

        if (groom == null) {
            errors.put("groom_identity_id", String.format("Data dengan NIK %s tidak ditemukan.", request.getGroomIdentityId()));
        }

        if (bride == null) {
            errors.put("bride_identity_id", String.format("Data dengan NIK %s tidak ditemukan.", request.getBrideIdentityId()));
        }

        if (!errors.isEmpty()) {
            ErrorUtil.throwErrors("Data calon pengantin tidak ditemukan.", HttpStatus.NOT_FOUND, errors);
        }

        if (!groom.getApplication().getId().equals(bride.getApplication().getId())) {
            errors.put("data_mismatch", "Data NIK mempelai pria dan wanita tidak terdaftar dalam satu pengajuan yang sama.");
            ErrorUtil.throwErrors("Data pengantin tidak cocok.", HttpStatus.BAD_REQUEST, errors);
        }
    }

    private Application save(Application application) {
        return this.applicationRepository.save(application);
    }

    private void processBride(
        Application application,
        ApplicationCreateRequest request,
        BrideFather brideFather,
        BrideMother brideMother,
        Guardian guardian,
        PreviousPartner previousBridePartner
    ) {
        var bride = Bride
            .builder()
            .application(application)
            .brideMother(brideMother)
            .brideFather(brideFather)
            .guardian(guardian)
            .previousPartner(previousBridePartner)
            .firstName(request.getBride().getFirstName())
            .lastName(request.getBride().getLastName())
            .alias(request.getBride().getAlias())
            .identityId(request.getBride().getIdentityId())
            .birthPlace(request.getBride().getBirthPlace())
            .birthDate(request.getBride().getBirthDate())
            .gender(request.getBride().getGender())
            .job(request.getBride().getJob())
            .nationality(request.getBride().getNationality())
            .religion(request.getBride().getReligion())
            .maritalStatus(request.getBride().getMaritalStatus())
            .phoneNumber(request.getBride().getPhoneNumber())
            .provinceCode(request.getBride().getProvinceCode())
            .provinceName(request.getBride().getProvinceName())
            .cityCode(request.getBride().getCityCode())
            .cityName(request.getBride().getCityName())
            .districtCode(request.getBride().getDistrictCode())
            .districtName(request.getBride().getDistrictName())
            .subDistrictCode(request.getBride().getSubDistrictCode())
            .subDistrictName(request.getBride().getSubDistrictName())
            .address(request.getBride().getAddress())
            .rw(request.getBride().getRw())
            .rt(request.getBride().getRt())
            .zipCode(request.getBride().getZipCode())
            .build();

        this.brideService.save(bride);
    }

    private BrideFather processBrideFather(ApplicationCreateRequest request) {
        return this.brideFatherMapper.convert(request.getBrideFather());
    }

    private BrideMother processBrideMother(ApplicationCreateRequest request) {
        return this.brideMotherMapper.convert(request.getBrideMother());
    }

    private void processGroom(
        Application application,
        ApplicationCreateRequest request,
        GroomFather groomFather,
        GroomMother groomMother,
        PreviousPartner previousGroomPartner
    ) {
        var groom = Groom
            .builder()
            .application(application)
            .groomFather(groomFather)
            .groomMother(groomMother)
            .previousPartner(previousGroomPartner)
            .firstName(request.getGroom().getFirstName())
            .lastName(request.getGroom().getLastName())
            .alias(request.getGroom().getAlias())
            .identityId(request.getGroom().getIdentityId())
            .birthPlace(request.getGroom().getBirthPlace())
            .birthDate(request.getGroom().getBirthDate())
            .gender(request.getGroom().getGender())
            .job(request.getGroom().getJob())
            .nationality(request.getBride().getNationality())
            .religion(request.getBride().getReligion())
            .maritalStatus(request.getBride().getMaritalStatus())
            .phoneNumber(request.getBride().getPhoneNumber())
            .provinceCode(request.getBride().getProvinceCode())
            .provinceName(request.getBride().getProvinceName())
            .cityCode(request.getBride().getCityCode())
            .cityName(request.getBride().getCityName())
            .districtCode(request.getBride().getDistrictCode())
            .districtName(request.getBride().getDistrictName())
            .subDistrictCode(request.getBride().getSubDistrictCode())
            .subDistrictName(request.getBride().getSubDistrictName())
            .address(request.getBride().getAddress())
            .rw(request.getBride().getRw())
            .rt(request.getBride().getRt())
            .zipCode(request.getBride().getZipCode())
            .build();

        this.groomService.save(groom);
    }

    private GroomFather processGroomFather(ApplicationCreateRequest request) {
        return this.groomFatherMapper.convert(request.getGroomFather());
    }

    private GroomMother processGroomMother(ApplicationCreateRequest request) {
        return this.groomMotherMapper.convert(request.getGroomMother());
    }

    private Guardian processGuardian(ApplicationCreateRequest request) {
        return this.guardianMapper.convert(request.getGuardian());
    }

    private void processMarriage(MarriageCreateRequest request, Application application) {
        var marriage = this.marriageMapper.convert(request, application);

        this.marriageService.save(marriage);
    }

    private PreviousPartner processGroomPreviousPartner(ApplicationCreateRequest request) {
        var groomStatus = request.getGroom().getMaritalStatus();
        var previousPartnerDto = request.getPreviousGroomPartner();

        return this.processPreviousPartner(previousPartnerDto, groomStatus);
    }

    private PreviousPartner processBridePreviousPartner(ApplicationCreateRequest request) {
        var brideStatus = request.getGroom().getMaritalStatus();
        var previousPartnerDto = request.getPreviousBridePartner();

        return this.processPreviousPartner(previousPartnerDto, brideStatus);
    }

    private PreviousPartner processPreviousPartner(
        PreviousPartnerCreateRequest previousPartnerDto,
        MarriageConstant.MaritalStatus maritalStatus
    ) {
        boolean isNotSingle = !MarriageConstant.MaritalStatus.SINGLE.equals(maritalStatus);
        boolean isPreviousPartnerMissing = previousPartnerDto == null;

        if (isNotSingle && isPreviousPartnerMissing) {
            ErrorUtil.throwError(
                "Data pasangan sebelumnya wajib diisi jika status pernikahan bukan lajang",
                HttpStatus.BAD_REQUEST
            );
        }

        return (previousPartnerDto != null)
            ? this.previousPartnerMapper.convert(previousPartnerDto)
            : null;
    }
}
