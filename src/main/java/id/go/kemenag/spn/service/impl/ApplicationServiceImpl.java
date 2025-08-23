package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.constant.MarriageConstant;
import id.go.kemenag.spn.dto.application.request.ApplicationCreateRequest;
import id.go.kemenag.spn.dto.application.response.ApplicationResponse;
import id.go.kemenag.spn.dto.previouspartner.request.PreviousPartnerCreateRequest;
import id.go.kemenag.spn.entity.marriage.*;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.mapper.*;
import id.go.kemenag.spn.service.ApplicationService;
import id.go.kemenag.spn.service.BrideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    private GroomFatherMapper groomFatherMapper;

    @Autowired
    private GroomMotherMapper groomMotherMapper;

    @Autowired
    private GuardianMapper guardianMapper;

    @Autowired
    private PreviousPartnerMapper previousPartnerMapper;

    @Override
    public ApplicationResponse create(ApplicationCreateRequest request, String menu) {




        return null;
    }

    private Bride processBride(ApplicationCreateRequest request) {
        return null;
    }

    private BrideFather processBrideFather(ApplicationCreateRequest request) {
        return this.brideFatherMapper.convert(request.getBrideFather());
    }

    private BrideMother processBrideMother(ApplicationCreateRequest request) {
        return this.brideMotherMapper.convert(request.getBrideMother());
    }

    private Groom processGroom(ApplicationCreateRequest request) {
        return null;
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
            this.throwError(
                "Data pasangan sebelumnya wajib diisi jika status pernikahan bukan lajang",
                HttpStatus.BAD_REQUEST
            );
        }

        return (previousPartnerDto != null)
            ? this.previousPartnerMapper.convert(previousPartnerDto)
            : null;
    }

    private void throwError(String message, HttpStatus status) {
        throw new BusinessErrorException(status, message);
    }
}
