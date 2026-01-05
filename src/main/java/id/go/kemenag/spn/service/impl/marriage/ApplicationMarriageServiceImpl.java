package id.go.kemenag.spn.service.impl.marriage;

import id.go.kemenag.spn.constant.*;
import id.go.kemenag.spn.dto.application.request.*;
import id.go.kemenag.spn.dto.application.response.*;
import id.go.kemenag.spn.dto.bride.request.BrideFatherUpdateRequest;
import id.go.kemenag.spn.dto.bride.request.BrideMotherUpdateRequest;
import id.go.kemenag.spn.dto.bride.request.BrideUpdateRequest;
import id.go.kemenag.spn.dto.camunda.request.CamundaCompleteUserTaskRequest;
import id.go.kemenag.spn.dto.groom.request.GroomFatherUpdateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomMotherUpdateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomUpdateRequest;
import id.go.kemenag.spn.dto.guardian.request.GuardianUpdateRequest;
import id.go.kemenag.spn.dto.marriage.request.MarriageCreateRequest;
import id.go.kemenag.spn.dto.marriage.request.MarriageUpdateRequest;
import id.go.kemenag.spn.dto.previouspartner.request.PreviousPartnerCreateRequest;
import id.go.kemenag.spn.dto.previouspartner.request.PreviousPartnerUpdateRequest;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.UpdateHistory;
import id.go.kemenag.spn.entity.marriage.*;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.exception.BusinessErrorsException;
import id.go.kemenag.spn.mapper.*;
import id.go.kemenag.spn.service.*;
import id.go.kemenag.spn.service.marriage.ApplicationMarriageService;
import id.go.kemenag.spn.service.marriage.BrideService;
import id.go.kemenag.spn.service.marriage.GroomService;
import id.go.kemenag.spn.service.marriage.MarriageService;
import id.go.kemenag.spn.service.master.MasterService;
import id.go.kemenag.spn.util.CommonUtil;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationMarriageServiceImpl implements ApplicationMarriageService {

    @Autowired
    private BrideService brideService;

    @Autowired
    private BrideMapper brideMapper;

    @Autowired
    private BrideFatherMapper brideFatherMapper;

    @Autowired
    private BrideMotherMapper brideMotherMapper;

    @Autowired
    private GroomService groomService;

    @Autowired
    private GroomMapper groomMapper;

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
    private ApplicationService applicationService;

    @Autowired
    private MarriageService marriageService;

    @Autowired
    private CamundaService camundaService;

    @Autowired
    private ApplicationHandlerService applicationHandlerService;

    @Autowired
    private AuthService authService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private UpdateHistoryService updateHistoryService;

    @Autowired
    private UserService userService;

    @Override
    public ApplicationCreateResponse createMarriage(ApplicationMarriageCreateRequest request) {
        var checkApplicationExist = this.applicationService
            .findByBrideAndGroomIdentityId(
                request.getBride().getIdentityId(),
                request.getGroom().getIdentityId(),
                ApplicationConstant.Type.MARRIAGE
            );

        if (checkApplicationExist) {
            throw new BusinessErrorException(HttpStatus.BAD_REQUEST, "Pengajuan pernikahan dengan data pengantin tersebut sudah ada dalam sistem.");
        }

        var application = Application
            .builder()
            .type(ApplicationConstant.Type.MARRIAGE)
            .status(ApplicationConstant.Status.CREATED)
            .build();

        application = this.applicationService.save(application);

        var brideFather = this.processBrideFather(request);
        var brideMother = this.processBrideMother(request);
        var groomFather = this.processGroomFather(request);
        var groomMother = this.processGroomMother(request);
        var guardian = this.processGuardian(request);
        var previousGroomPartner = this.processGroomPreviousPartner(request);
        var previousBridePartner = this.processBridePreviousPartner(request);

        var cancelled = this.applicationHandlerService.setInitialMarriageHandler(application, request);

        var bride = this.processBride(application, request, brideFather, brideMother, guardian, previousBridePartner);
        var groom = this.processGroom(application, request, groomFather, groomMother, previousGroomPartner);
        var marriage = this.processMarriage(request.getMarriage(), application, bride, groom);

        var isMuslim = MarriageConstant.Religion.ISLAM.equals(bride.getReligion())
            && MarriageConstant.Religion.ISLAM.equals(groom.getReligion());

        var processId = this.camundaService.invokeMarriageProcess(
            cancelled,
            marriage,
            isMuslim
        );
        application.setProcessId(processId);
        application = this.applicationService.save(application);

        return ApplicationCreateResponse
            .builder()
            .applicationId(application.getId())
            .processId(processId)
            .status(cancelled ? ApplicationConstant.Status.CANCELLED : ApplicationConstant.Status.CREATED)
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

    @Override
    public List<ApplicationMarriageResponse> findAllApplication() {
        var applications = this.applicationService
            .findAllByStatusInAndType(
                List.of(ApplicationConstant.Status.PROCESSED),
                ApplicationConstant.Type.MARRIAGE
            );

        if (applications.isEmpty()) {
            return List.of();
        }

        var applicationIds = this.applicationService.collectIds(applications);
        var brides = this.brideService.findAllByApplicationIds(applicationIds);
        var grooms = this.groomService.findAllByApplicationIds(applicationIds);
        var marriages = this.marriageService.findAllByApplicationIds(applicationIds);

        Map<UUID, Bride> brideMap = brides
            .stream()
            .collect(Collectors.toMap(b -> b.getApplication().getId(), Function.identity()));

        Map<UUID, Groom> groomMap = grooms
            .stream()
            .collect(Collectors.toMap(g -> g.getApplication().getId(), Function.identity()));

        Map<UUID, Marriage> marriageMap = marriages
            .stream()
            .collect(Collectors.toMap(m -> m.getApplication().getId(), Function.identity()));

        return applications
            .stream()
            .map(application -> {
                var bride = brideMap.get(application.getId());
                var groom = groomMap.get(application.getId());
                var marriage = marriageMap.get(application.getId());

                if (bride != null && groom != null && marriage != null) {
                    return this.buildApplicationMarriageResponse(application, bride, groom, marriage);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public ApplicationMarriageResponse findApplicationById(UUID applicationId) {
        var application = this.applicationService.findById(applicationId);
        if (application == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Application not found");
        }

        var marriage = this.marriageService.findByApplicationId(applicationId);
        if (marriage == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Marriage not found for application");
        }

        return this.buildApplicationMarriageResponse(
            application,
            marriage.getBride(),
            marriage.getGroom(),
            marriage
        );
    }

    @Override
    public List<ApplicationMarriageResponse> findAllApplicationBasedOnHandler() {
        var user = this.authService.getCurrentUser();
        if (user == null) {
            throw new BusinessErrorException(HttpStatus.FORBIDDEN, "You are not authorized to access application");
        }

        var applications = this.applicationService
            .findAllABasedOnHandler(
                List.of(ApplicationConstant.Status.PROCESSED),
                ApplicationConstant.Type.MARRIAGE,
                user.getRole(),
                user.getWorkplaceCode()
            );

        if (applications.isEmpty()) {
            return List.of();
        }

        var applicationIds = this.applicationService.collectIds(applications);
        var brides = this.brideService.findAllByApplicationIds(applicationIds);
        var grooms = this.groomService.findAllByApplicationIds(applicationIds);
        var marriages = this.marriageService.findAllByApplicationIds(applicationIds);

        Map<UUID, Bride> brideMap = brides
            .stream()
            .collect(Collectors.toMap(b -> b.getApplication().getId(), Function.identity()));

        Map<UUID, Groom> groomMap = grooms
            .stream()
            .collect(Collectors.toMap(g -> g.getApplication().getId(), Function.identity()));

        Map<UUID, Marriage> marriageMap = marriages
            .stream()
            .collect(Collectors.toMap(m -> m.getApplication().getId(), Function.identity()));

        return applications
            .stream()
            .map(application -> {
                var bride = brideMap.get(application.getId());
                var groom = groomMap.get(application.getId());
                var marriage = marriageMap.get(application.getId());

                if (bride != null && groom != null && marriage != null) {
                    return this.buildApplicationMarriageResponse(application, bride, groom, marriage);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public ApplicationMarriageResponse findApplicationByIdBasedOnHandler(UUID applicationId) {
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

        var marriage = this.marriageService.findByApplicationId(applicationId);
        if (marriage == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Marriage not found for application");
        }

        return this.buildApplicationMarriageResponse(
            application,
            marriage.getBride(),
            marriage.getGroom(),
            marriage
        );
    }

    @Override
    public ApplicationMarriageApproveResponse approveApplication(ApplicationMarriageApproveRequest request) {
        var marriage = this.marriageService.findByApplicationId(request.getApplicationId());
        if (marriage == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Application not found");
        }

        var handler = this.applicationHandlerService.validateHandler(request.getApplicationId());

        Map<String, Object>  resultMap = new HashMap<>();

        List<String> taskNames = new ArrayList<>(
            List.of(
                ActivityIdConstant.ACTIVITY_VILLAGE_1_REGISTRAR,
                ActivityIdConstant.ACTIVITY_VILLAGE_1_HEADMAN,
                ActivityIdConstant.ACTIVITY_RELIGIOUS_AFFAIRS_1_OFFICER,
                ActivityIdConstant.ACTIVITY_RELIGIOUS_AFFAIRS_1_APPROVER,
                ActivityIdConstant.ACTIVITY_VILLAGE_2_REGISTRAR,
                ActivityIdConstant.ACTIVITY_VILLAGE_2_HEADMAN,
                ActivityIdConstant.ACTIVITY_RELIGIOUS_AFFAIRS_2_OFFICER,
                ActivityIdConstant.ACTIVITY_RELIGIOUS_AFFAIRS_2_APPROVER
            )
        );

        resultMap.put("approvedStatus", request.getApprovedStatus().name());

        this.camundaService.completeUserTask(
            CamundaCompleteUserTaskRequest
                .builder()
                .processInstanceId(String.valueOf(marriage.getApplication().getProcessId()))
                .taskNames(taskNames)
                .resultMap(resultMap)
                .build(),
            handler
        );

        return ApplicationMarriageApproveResponse
            .builder()
            .applicationId(marriage.getApplication().getId())
            .approvedStatus(request.getApprovedStatus())
            .build();
    }

    @Override
    public ApplicationMarriageUpdateResponse updateApplicationById(UUID applicationId, ApplicationMarriageUpdateRequest request) {
        var marriage = this.marriageService.findByApplicationId(applicationId);
        if (marriage == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Application not found");
        }

        var user = this.authService.getCurrentUser();
        var userFullName = user.getUsername();
        var userDetails = this.userService.findByUsername(userFullName);
        if (userDetails != null) {
            userFullName = CommonUtil.buildFullName(
                userDetails.getFirstName(), userDetails.getLastName(), null
            );
        }

        List<UpdateHistory> updateHistories = new ArrayList<>();

        var bride = marriage.getBride();
        var brideMother = bride.getBrideMother();
        var brideFather = bride.getBrideFather();
        var groom = marriage.getGroom();
        var groomMother = groom.getGroomMother();
        var groomFather = groom.getGroomFather();
        var guardian = bride.getGuardian();
        var previousGroomPartner = groom.getPreviousPartner();
        var previousBridePartner = bride.getPreviousPartner();

        this.updateGroomFather(applicationId, request.getGroomFather(), groomFather, updateHistories, userFullName);
        this.updateGroomMother(applicationId, request.getGroomMother(), groomMother, updateHistories, userFullName);
        this.updateBrideFather(applicationId, request.getBrideFather(), brideFather, updateHistories, userFullName);
        this.updateBrideMother(applicationId, request.getBrideMother(), brideMother, updateHistories, userFullName);
        this.updateGuardian(applicationId, request.getGuardian(), guardian, updateHistories, userFullName);
        this.updatePreviousPartner(applicationId, request.getPreviousGroomPartner(), previousGroomPartner, true, updateHistories, userFullName);
        this.updatePreviousPartner(applicationId, request.getPreviousBridePartner(), previousBridePartner, false, updateHistories, userFullName);
        this.updateGroom(applicationId, request.getGroom(), groom, updateHistories, userFullName);
        this.updateBride(applicationId, request.getBride(), bride, updateHistories, userFullName);
        this.updateMarriage(applicationId, request.getMarriage(), marriage, updateHistories, userFullName);

        this.groomFatherMapper.copy(request.getGroomFather(), marriage.getGroom().getGroomFather());
        this.groomMotherMapper.copy(request.getGroomMother(), marriage.getGroom().getGroomMother());
        this.brideFatherMapper.copy(request.getBrideFather(), marriage.getBride().getBrideFather());
        this.brideMotherMapper.copy(request.getBrideMother(), marriage.getBride().getBrideMother());
        this.guardianMapper.copy(request.getGuardian(), marriage.getBride().getGuardian());
        this.previousPartnerMapper.copy(request.getPreviousGroomPartner(), marriage.getGroom().getPreviousPartner());
        this.previousPartnerMapper.copy(request.getPreviousBridePartner(), marriage.getBride().getPreviousPartner());

        this.groomMapper.copy(request.getGroom(), marriage.getGroom());
        this.brideMapper.copy(request.getBride(), marriage.getBride());

        this.marriageMapper.copy(request.getMarriage(), marriage);

        this.marriageService.save(marriage);
        this.updateHistoryService.saveAll(updateHistories);

        return ApplicationMarriageUpdateResponse
            .builder()
            .applicationId(applicationId)
            .build();
    }

    private void updateMarriage(
        UUID applicationId,
        MarriageUpdateRequest request,
        Marriage marriage,
        List<UpdateHistory> updateHistories,
        String userFullName
    ) {
        if (request == null) return;
        var oldMarriageDate = marriage.getDatetime() != null
            ? CommonUtil.normalizeDateTime(marriage.getDatetime())
            : null;
        var newMarriageDate = request.getDatetime() != null
            ? CommonUtil.normalizeDateTime(request.getDatetime())
            : null;
        if (!Objects.equals(oldMarriageDate, newMarriageDate)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("marriage_time");
            updateHistory.setOldValue(oldMarriageDate);
            updateHistory.setNewValue(newMarriageDate);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldMarriagePlace = CommonUtil.buildFullAddress(
            marriage.getAddress(),
            marriage.getRt(),
            marriage.getRw(),
            marriage.getSubDistrictName(),
            marriage.getDistrictName(),
            marriage.getCityName(),
            marriage.getProvinceName(),
            marriage.getZipCode()
        );
        var newMarriagePlace = CommonUtil.buildFullAddress(
            request.getAddress(),
            request.getRt(),
            request.getRw(),
            request.getSubDistrictName(),
            request.getDistrictName(),
            request.getCityName(),
            request.getProvinceName(),
            request.getZipCode()
        );
        if (!Objects.equals(oldMarriagePlace, newMarriagePlace)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("marriage_location");
            updateHistory.setOldValue(oldMarriagePlace);
            updateHistory.setNewValue(newMarriagePlace);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldDowry = marriage.getDowry();
        var newDowry = request.getDowry();
        if (!Objects.equals(oldDowry, newDowry)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("marriage_dowry");
            updateHistory.setOldValue(oldDowry);
            updateHistory.setNewValue(newDowry);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }
    }

    private void updateBride(
        UUID applicationId,
        BrideUpdateRequest request,
        Bride bride,
        List<UpdateHistory> updateHistories,
        String userFullName
    ) {
        if (request == null) return;
        var oldFullName = CommonUtil.buildFullName(
            bride.getFirstName(),
            bride.getLastName(),
            bride.getAlias()
        );
        var newFullName = CommonUtil.buildFullName(
            request.getFirstName(),
            request.getLastName(),
            request.getAlias()
        );
        if (!Objects.equals(oldFullName, newFullName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_full_name");
            updateHistory.setOldValue(oldFullName);
            updateHistory.setNewValue(newFullName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldIdentityId = bride.getIdentityId();
        var newIdentityId = request.getIdentityId();
        if (!Objects.equals(oldIdentityId, newIdentityId)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_identity_id");
            updateHistory.setOldValue(oldIdentityId);
            updateHistory.setNewValue(newIdentityId);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldBirthInfo = CommonUtil.buildBirthInfo(
            bride.getBirthPlace(),
            bride.getBirthDate()
        );
        var newBirthInfo = CommonUtil.buildBirthInfo(
            request.getBirthPlace(),
            request.getBirthDate()
        );
        if (!Objects.equals(oldBirthInfo, newBirthInfo)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_birth_info");
            updateHistory.setOldValue(oldBirthInfo);
            updateHistory.setNewValue(newBirthInfo);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldAddress = CommonUtil.buildFullAddress(
            bride.getAddress(),
            bride.getRt(),
            bride.getRw(),
            bride.getSubDistrictName(),
            bride.getDistrictName(),
            bride.getCityName(),
            bride.getProvinceName(),
            bride.getZipCode()
        );
        var newAddress = CommonUtil.buildFullAddress(
            request.getAddress(),
            request.getRt(),
            request.getRw(),
            request.getSubDistrictName(),
            request.getDistrictName(),
            request.getCityName(),
            request.getProvinceName(),
            request.getZipCode()
        );
        if (!Objects.equals(oldAddress, newAddress)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_address");
            updateHistory.setOldValue(oldAddress);
            updateHistory.setNewValue(newAddress);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }
    }

    private void updateGroom(
        UUID applicationId,
        GroomUpdateRequest request,
        Groom groom,
        List<UpdateHistory> updateHistories,
        String userFullName
    ) {
        if (request == null) return;
        var oldFullName = CommonUtil.buildFullName(
            groom.getFirstName(),
            groom.getLastName(),
            groom.getAlias()
        );
        var newFullName = CommonUtil.buildFullName(
            request.getFirstName(),
            request.getLastName(),
            request.getAlias()
        );
        if (!Objects.equals(oldFullName, newFullName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_full_name");
            updateHistory.setOldValue(oldFullName);
            updateHistory.setNewValue(newFullName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldIdentityId = groom.getIdentityId();
        var newIdentityId = request.getIdentityId();
        if (!Objects.equals(oldIdentityId, newIdentityId)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_identity_id");
            updateHistory.setOldValue(oldIdentityId);
            updateHistory.setNewValue(newIdentityId);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldBirthInfo = CommonUtil.buildBirthInfo(
            groom.getBirthPlace(),
            groom.getBirthDate()
        );
        var newBirthInfo = CommonUtil.buildBirthInfo(
            request.getBirthPlace(),
            request.getBirthDate()
        );
        if (!Objects.equals(oldBirthInfo, newBirthInfo)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_birth_info");
            updateHistory.setOldValue(oldBirthInfo);
            updateHistory.setNewValue(newBirthInfo);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldAddress = CommonUtil.buildFullAddress(
            groom.getAddress(),
            groom.getRt(),
            groom.getRw(),
            groom.getSubDistrictName(),
            groom.getDistrictName(),
            groom.getCityName(),
            groom.getProvinceName(),
            groom.getZipCode()
        );
        var newAddress = CommonUtil.buildFullAddress(
            request.getAddress(),
            request.getRt(),
            request.getRw(),
            request.getSubDistrictName(),
            request.getDistrictName(),
            request.getCityName(),
            request.getProvinceName(),
            request.getZipCode()
        );
        if (!Objects.equals(oldAddress, newAddress)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_address");
            updateHistory.setOldValue(oldAddress);
            updateHistory.setNewValue(newAddress);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

    }

    private void updatePreviousPartner(
        UUID applicationId,
        PreviousPartnerUpdateRequest request,
        PreviousPartner previousPartner,
        Boolean isGroom,
        List<UpdateHistory> updateHistories,
        String userFullName
    ) {
        if (request == null) return;
        var oldFullName = CommonUtil.buildFullName(
            previousPartner.getFirstName(),
            previousPartner.getLastName(),
            previousPartner.getAlias()
        );
        var newFullName = CommonUtil.buildFullName(
            request.getFirstName(),
            request.getLastName(),
            request.getAlias()
        );
        var labelPrefix = isGroom ? "previous_groom_partner_" : "previous_bride_partner_";
        if (!Objects.equals(oldFullName, newFullName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel(labelPrefix + "full_name");
            updateHistory.setOldValue(oldFullName);
            updateHistory.setNewValue(newFullName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldBirthInfo = CommonUtil.buildBirthInfo(
            previousPartner.getBirthPlace(),
            previousPartner.getBirthDate()
        );
        var newBirthInfo = CommonUtil.buildBirthInfo(
            request.getBirthPlace(),
            request.getBirthDate()
        );
        if (!Objects.equals(oldBirthInfo, newBirthInfo)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel(labelPrefix + "birth_info");
            updateHistory.setOldValue(oldBirthInfo);
            updateHistory.setNewValue(newBirthInfo);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldDeathInfo = CommonUtil.buildBirthInfo(
            previousPartner.getBirthPlace(),
            previousPartner.getBirthDate()
        );
        var newDeathInfo = CommonUtil.buildBirthInfo(
            request.getBirthPlace(),
            request.getBirthDate()
        );
        if (!Objects.equals(oldDeathInfo, newDeathInfo)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel(labelPrefix + "death_info");
            updateHistory.setOldValue(oldDeathInfo);
            updateHistory.setNewValue(newDeathInfo);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldIdentityId = previousPartner.getIdentityId();
        var newIdentityId = request.getIdentityId();
        if (!Objects.equals(oldIdentityId, newIdentityId)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel(labelPrefix + "identity_id");
            updateHistory.setOldValue(oldIdentityId);
            updateHistory.setNewValue(newIdentityId);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldFatherName = previousPartner.getFatherName();
        var newFatherName = request.getFatherName();
        if (!Objects.equals(oldFatherName, newFatherName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel(labelPrefix + "father_name");
            updateHistory.setOldValue(oldFatherName);
            updateHistory.setNewValue(newFatherName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldAddress = CommonUtil.buildFullAddress(
            previousPartner.getAddress(),
            previousPartner.getRt(),
            previousPartner.getRw(),
            previousPartner.getSubDistrictName(),
            previousPartner.getDistrictName(),
            previousPartner.getCityName(),
            previousPartner.getProvinceName(),
            previousPartner.getZipCode()
        );
        var newAddress = CommonUtil.buildFullAddress(
            request.getAddress(),
            request.getRt(),
            request.getRw(),
            request.getSubDistrictName(),
            request.getDistrictName(),
            request.getCityName(),
            request.getProvinceName(),
            request.getZipCode()
        );
        if (!Objects.equals(oldAddress, newAddress)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel(labelPrefix + "address");
            updateHistory.setOldValue(oldAddress);
            updateHistory.setNewValue(newAddress);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }
    }

    private void updateGuardian(
        UUID applicationId,
        GuardianUpdateRequest request,
        Guardian guardian,
        List<UpdateHistory> updateHistories,
        String userFullName
    ) {
        if (request == null) return;
        var oldFullName = CommonUtil.buildFullName(
            guardian.getFirstName(),
            guardian.getLastName(),
            guardian.getAlias()
        );
        var newFullName = CommonUtil.buildFullName(
            request.getFirstName(),
            request.getLastName(),
            request.getAlias()
        );
        if (!Objects.equals(oldFullName, newFullName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("guardian_full_name");
            updateHistory.setOldValue(oldFullName);
            updateHistory.setNewValue(newFullName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldBirthInfo = CommonUtil.buildBirthInfo(
            guardian.getBirthPlace(),
            guardian.getBirthDate()
        );
        var newBirthInfo = CommonUtil.buildBirthInfo(
            request.getBirthPlace(),
            request.getBirthDate()
        );
        if (!Objects.equals(oldBirthInfo, newBirthInfo)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("guardian_birth_info");
            updateHistory.setOldValue(oldBirthInfo);
            updateHistory.setNewValue(newBirthInfo);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldIdentityId = guardian.getIdentityId();
        var newIdentityId = request.getIdentityId();
        if (!Objects.equals(oldIdentityId, newIdentityId)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("guardian_identity_id");
            updateHistory.setOldValue(oldIdentityId);
            updateHistory.setNewValue(newIdentityId);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldFatherName = guardian.getFatherName();
        var newFatherName = request.getFatherName();
        if (!Objects.equals(oldFatherName, newFatherName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("guardian_father_name");
            updateHistory.setOldValue(oldFatherName);
            updateHistory.setNewValue(newFatherName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldAddress = CommonUtil.buildFullAddress(
            guardian.getAddress(),
            guardian.getRt(),
            guardian.getRw(),
            guardian.getSubDistrictName(),
            guardian.getDistrictName(),
            guardian.getCityName(),
            guardian.getProvinceName(),
            guardian.getZipCode()
        );
        var newAddress = CommonUtil.buildFullAddress(
            request.getAddress(),
            request.getRt(),
            request.getRw(),
            request.getSubDistrictName(),
            request.getDistrictName(),
            request.getCityName(),
            request.getProvinceName(),
            request.getZipCode()
        );
        if (!Objects.equals(oldAddress, newAddress)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("guardian_address");
            updateHistory.setOldValue(oldAddress);
            updateHistory.setNewValue(newAddress);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }
    }

    private void updateBrideMother(
        UUID applicationId,
        BrideMotherUpdateRequest request,
        BrideMother brideMother,
        List<UpdateHistory> updateHistories,
        String userFullName
    ) {
        if (request == null) return;
        var oldFullName = CommonUtil.buildFullName(
            brideMother.getFirstName(),
            brideMother.getLastName(),
            brideMother.getAlias()
        );
        var newFullName = CommonUtil.buildFullName(
            request.getFirstName(),
            request.getLastName(),
            request.getAlias()
        );
        if (!Objects.equals(oldFullName, newFullName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_mother_full_name");
            updateHistory.setOldValue(oldFullName);
            updateHistory.setNewValue(newFullName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldBirthInfo = CommonUtil.buildBirthInfo(
            brideMother.getBirthPlace(),
            brideMother.getBirthDate()
        );
        var newBirthInfo = CommonUtil.buildBirthInfo(
            request.getBirthPlace(),
            request.getBirthDate()
        );
        if (!Objects.equals(oldBirthInfo, newBirthInfo)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_mother_birth_info");
            updateHistory.setOldValue(oldBirthInfo);
            updateHistory.setNewValue(newBirthInfo);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldIdentityId = brideMother.getIdentityId();
        var newIdentityId = request.getIdentityId();
        if (!Objects.equals(oldIdentityId, newIdentityId)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_mother_identity_id");
            updateHistory.setOldValue(oldIdentityId);
            updateHistory.setNewValue(newIdentityId);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldFatherName = brideMother.getFatherName();
        var newFatherName = request.getFatherName();
        if (!Objects.equals(oldFatherName, newFatherName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_mother_father_name");
            updateHistory.setOldValue(oldFatherName);
            updateHistory.setNewValue(newFatherName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldAddress = CommonUtil.buildFullAddress(
            brideMother.getAddress(),
            brideMother.getRt(),
            brideMother.getRw(),
            brideMother.getSubDistrictName(),
            brideMother.getDistrictName(),
            brideMother.getCityName(),
            brideMother.getProvinceName(),
            brideMother.getZipCode()
        );
        var newAddress = CommonUtil.buildFullAddress(
            request.getAddress(),
            request.getRt(),
            request.getRw(),
            request.getSubDistrictName(),
            request.getDistrictName(),
            request.getCityName(),
            request.getProvinceName(),
            request.getZipCode()
        );
        if (!Objects.equals(oldAddress, newAddress)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_mother_address");
            updateHistory.setOldValue(oldAddress);
            updateHistory.setNewValue(newAddress);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }
    }

    private void updateBrideFather(
        UUID applicationId,
        BrideFatherUpdateRequest request,
        BrideFather brideFather,
        List<UpdateHistory> updateHistories,
        String userFullName
    ) {
        if (request == null) return;
        var oldFullName = CommonUtil.buildFullName(
            brideFather.getFirstName(),
            brideFather.getLastName(),
            brideFather.getAlias()
        );
        var newFullName = CommonUtil.buildFullName(
            request.getFirstName(),
            request.getLastName(),
            request.getAlias()
        );
        if (!Objects.equals(oldFullName, newFullName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_father_full_name");
            updateHistory.setOldValue(oldFullName);
            updateHistory.setNewValue(newFullName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldBirthInfo = CommonUtil.buildBirthInfo(
            brideFather.getBirthPlace(),
            brideFather.getBirthDate()
        );
        var newBirthInfo = CommonUtil.buildBirthInfo(
            request.getBirthPlace(),
            request.getBirthDate()
        );
        if (!Objects.equals(oldBirthInfo, newBirthInfo)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_father_birth_info");
            updateHistory.setOldValue(oldBirthInfo);
            updateHistory.setNewValue(newBirthInfo);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldIdentityId = brideFather.getIdentityId();
        var newIdentityId = request.getIdentityId();
        if (!Objects.equals(oldIdentityId, newIdentityId)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_father_identity_id");
            updateHistory.setOldValue(oldIdentityId);
            updateHistory.setNewValue(newIdentityId);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldFatherName = brideFather.getFatherName();
        var newFatherName = request.getFatherName();
        if (!Objects.equals(oldFatherName, newFatherName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_father_father_name");
            updateHistory.setOldValue(oldFatherName);
            updateHistory.setNewValue(newFatherName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldAddress = CommonUtil.buildFullAddress(
            brideFather.getAddress(),
            brideFather.getRt(),
            brideFather.getRw(),
            brideFather.getSubDistrictName(),
            brideFather.getDistrictName(),
            brideFather.getCityName(),
            brideFather.getProvinceName(),
            brideFather.getZipCode()
        );
        var newAddress = CommonUtil.buildFullAddress(
            request.getAddress(),
            request.getRt(),
            request.getRw(),
            request.getSubDistrictName(),
            request.getDistrictName(),
            request.getCityName(),
            request.getProvinceName(),
            request.getZipCode()
        );
        if (!Objects.equals(oldAddress, newAddress)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("bride_father_address");
            updateHistory.setOldValue(oldAddress);
            updateHistory.setNewValue(newAddress);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }
    }

    private void updateGroomFather(
        UUID applicationId,
        GroomFatherUpdateRequest request,
        GroomFather groomFather,
        List<UpdateHistory> updateHistories,
        String userFullName
    ) {
        if (request == null) return;
        var oldFullName = CommonUtil.buildFullName(
            groomFather.getFirstName(),
            groomFather.getLastName(),
            groomFather.getAlias()
        );
        var newFullName = CommonUtil.buildFullName(
            request.getFirstName(),
            request.getLastName(),
            request.getAlias()
        );
        if (!Objects.equals(oldFullName, newFullName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_father_full_name");
            updateHistory.setOldValue(oldFullName);
            updateHistory.setNewValue(newFullName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldBirthInfo = CommonUtil.buildBirthInfo(
            groomFather.getBirthPlace(),
            groomFather.getBirthDate()
        );
        var newBirthInfo = CommonUtil.buildBirthInfo(
            request.getBirthPlace(),
            request.getBirthDate()
        );
        if (!Objects.equals(oldBirthInfo, newBirthInfo)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_father_birth_info");
            updateHistory.setOldValue(oldBirthInfo);
            updateHistory.setNewValue(newBirthInfo);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldIdentityId = groomFather.getIdentityId();
        var newIdentityId = request.getIdentityId();
        if (!Objects.equals(oldIdentityId, newIdentityId)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_father_identity_id");
            updateHistory.setOldValue(oldIdentityId);
            updateHistory.setNewValue(newIdentityId);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldFatherName = groomFather.getFatherName();
        var newFatherName = request.getFatherName();
        if (!Objects.equals(oldFatherName, newFatherName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_father_father_name");
            updateHistory.setOldValue(oldFatherName);
            updateHistory.setNewValue(newFatherName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldAddress = CommonUtil.buildFullAddress(
            groomFather.getAddress(),
            groomFather.getRt(),
            groomFather.getRw(),
            groomFather.getSubDistrictName(),
            groomFather.getDistrictName(),
            groomFather.getCityName(),
            groomFather.getProvinceName(),
            groomFather.getZipCode()
        );
        var newAddress = CommonUtil.buildFullAddress(
            request.getAddress(),
            request.getRt(),
            request.getRw(),
            request.getSubDistrictName(),
            request.getDistrictName(),
            request.getCityName(),
            request.getProvinceName(),
            request.getZipCode()
        );
        if (!Objects.equals(oldAddress, newAddress)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_father_address");
            updateHistory.setOldValue(oldAddress);
            updateHistory.setNewValue(newAddress);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }
    }

    private void updateGroomMother(
        UUID applicationId,
        GroomMotherUpdateRequest request,
        GroomMother groomMother,
        List<UpdateHistory> updateHistories,
        String userFullName
    ) {
        if (request == null) return;
        var oldFullName = CommonUtil.buildFullName(
            groomMother.getFirstName(),
            groomMother.getLastName(),
            groomMother.getAlias()
        );
        var newFullName = CommonUtil.buildFullName(
            request.getFirstName(),
            request.getLastName(),
            request.getAlias()
        );
        if (!Objects.equals(oldFullName, newFullName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_mother_full_name");
            updateHistory.setOldValue(oldFullName);
            updateHistory.setNewValue(newFullName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldBirthInfo = CommonUtil.buildBirthInfo(
            groomMother.getBirthPlace(),
            groomMother.getBirthDate()
        );
        var newBirthInfo = CommonUtil.buildBirthInfo(
            request.getBirthPlace(),
            request.getBirthDate()
        );
        if (!Objects.equals(oldBirthInfo, newBirthInfo)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_mother_birth_info");
            updateHistory.setOldValue(oldBirthInfo);
            updateHistory.setNewValue(newBirthInfo);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldIdentityId = groomMother.getIdentityId();
        var newIdentityId = request.getIdentityId();
        if (!Objects.equals(oldIdentityId, newIdentityId)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_mother_identity_id");
            updateHistory.setOldValue(oldIdentityId);
            updateHistory.setNewValue(newIdentityId);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldFatherName = groomMother.getFatherName();
        var newFatherName = request.getFatherName();
        if (!Objects.equals(oldFatherName, newFatherName)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_mother_father_name");
            updateHistory.setOldValue(oldFatherName);
            updateHistory.setNewValue(newFatherName);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }

        var oldAddress = CommonUtil.buildFullAddress(
            groomMother.getAddress(),
            groomMother.getRt(),
            groomMother.getRw(),
            groomMother.getSubDistrictName(),
            groomMother.getDistrictName(),
            groomMother.getCityName(),
            groomMother.getProvinceName(),
            groomMother.getZipCode()
        );
        var newAddress = CommonUtil.buildFullAddress(
            request.getAddress(),
            request.getRt(),
            request.getRw(),
            request.getSubDistrictName(),
            request.getDistrictName(),
            request.getCityName(),
            request.getProvinceName(),
            request.getZipCode()
        );
        if (!Objects.equals(oldAddress, newAddress)) {
            var updateHistory = new UpdateHistory();
            updateHistory.setApplicationId(applicationId);
            updateHistory.setLabel("groom_mother_address");
            updateHistory.setOldValue(oldAddress);
            updateHistory.setNewValue(newAddress);
            updateHistory.setUpdatedBy(userFullName);
            updateHistories.add(updateHistory);
        }
    }

    @Override
    public byte[] downloadMarriageDocument(UUID applicationId) {
        var user = this.authService.getCurrentUser();

        var marriage = this.marriageService.findByApplicationId(applicationId);
        if (marriage == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Application not found");
        }

        var userWorkplaceCode = user.getWorkplaceCode();
        var brideSubDistrictCode = marriage.getBride().getSubDistrictCode();
        var groomSubDistrictCode = marriage.getGroom().getSubDistrictCode();

        DocumentConstant.BundleMarriageType bundleType;
        if (Objects.equals(userWorkplaceCode, brideSubDistrictCode) && Objects.equals(userWorkplaceCode, groomSubDistrictCode)) {
            bundleType = DocumentConstant.BundleMarriageType.COMPLETE;
        } else if (Objects.equals(userWorkplaceCode, brideSubDistrictCode)) {
            bundleType = DocumentConstant.BundleMarriageType.BRIDE_ONLY;
        } else if (Objects.equals(userWorkplaceCode, groomSubDistrictCode)) {
            bundleType = DocumentConstant.BundleMarriageType.GROOM_ONLY;
        } else {
            throw new BusinessErrorException(HttpStatus.FORBIDDEN, "You are not authorized to download this document");
        }

        return this.documentService.downloadMarriageDocument(marriage, user, bundleType);
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
            throw new BusinessErrorsException(HttpStatus.NOT_FOUND, "Data calon pengantin tidak ditemukan.", errors);
        }

        if (!groom.getApplication().getId().equals(bride.getApplication().getId())) {
            errors.put("data_mismatch", "Data calon pengantin tidak terdaftar dalam satu pengajuan yang sama.");
            throw new BusinessErrorsException(HttpStatus.BAD_REQUEST, "Data pengantin tidak cocok.", errors);
        }
    }

    private Bride processBride(
        Application application,
        ApplicationMarriageCreateRequest request,
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

        return this.brideService.save(bride);
    }

    private BrideFather processBrideFather(ApplicationMarriageCreateRequest request) {
        return this.brideFatherMapper.convert(request.getBrideFather());
    }

    private BrideMother processBrideMother(ApplicationMarriageCreateRequest request) {
        return this.brideMotherMapper.convert(request.getBrideMother());
    }

    private Groom processGroom(
        Application application,
        ApplicationMarriageCreateRequest request,
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
            .nationality(request.getGroom().getNationality())
            .religion(request.getGroom().getReligion())
            .maritalStatus(request.getGroom().getMaritalStatus())
            .phoneNumber(request.getGroom().getPhoneNumber())
            .provinceCode(request.getGroom().getProvinceCode())
            .provinceName(request.getGroom().getProvinceName())
            .cityCode(request.getGroom().getCityCode())
            .cityName(request.getGroom().getCityName())
            .districtCode(request.getGroom().getDistrictCode())
            .districtName(request.getGroom().getDistrictName())
            .subDistrictCode(request.getGroom().getSubDistrictCode())
            .subDistrictName(request.getGroom().getSubDistrictName())
            .address(request.getGroom().getAddress())
            .rw(request.getGroom().getRw())
            .rt(request.getGroom().getRt())
            .zipCode(request.getGroom().getZipCode())
            .build();

        return this.groomService.save(groom);
    }

    private GroomFather processGroomFather(ApplicationMarriageCreateRequest request) {
        return this.groomFatherMapper.convert(request.getGroomFather());
    }

    private GroomMother processGroomMother(ApplicationMarriageCreateRequest request) {
        return this.groomMotherMapper.convert(request.getGroomMother());
    }

    private Guardian processGuardian(ApplicationMarriageCreateRequest request) {
        return this.guardianMapper.convert(request.getGuardian());
    }

    private Marriage processMarriage(
        MarriageCreateRequest request, Application application, Bride bride, Groom groom
    ) {
        var marriage = this.marriageMapper.convert(request);
        marriage.setApplication(application);
        marriage.setBride(bride);
        marriage.setGroom(groom);

        return this.marriageService.save(marriage);
    }

    private PreviousPartner processGroomPreviousPartner(ApplicationMarriageCreateRequest request) {
        var groomStatus = request.getGroom().getMaritalStatus();
        var previousPartnerDto = request.getPreviousGroomPartner();

        return this.processPreviousPartner(previousPartnerDto, groomStatus);
    }

    private PreviousPartner processBridePreviousPartner(ApplicationMarriageCreateRequest request) {
        var brideStatus = request.getBride().getMaritalStatus();
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
            throw new BusinessErrorException(HttpStatus.BAD_REQUEST,
                "Data pasangan sebelumnya wajib diisi jika status pernikahan bukan lajang"
            );
        }

        return (previousPartnerDto != null)
            ? this.previousPartnerMapper.convert(previousPartnerDto)
            : null;
    }

    private ApplicationMarriageResponse buildApplicationMarriageResponse(
        Application application,
        Bride bride,
        Groom groom,
        Marriage marriage
    ) {
        var brideResponse = this.brideMapper.convert(bride);
        var brideFatherResponse = this.brideFatherMapper.convert(bride.getBrideFather());
        var brideMotherResponse = this.brideMotherMapper.convert(bride.getBrideMother());
        var brideGuardianResponse = this.guardianMapper.convert(bride.getGuardian());
        var bridePreviousPartnerResponse = this.previousPartnerMapper.convert(bride.getPreviousPartner());
        var groomResponse = this.groomMapper.convert(groom);
        var groomFatherResponse = this.groomFatherMapper.convert(groom.getGroomFather());
        var groomMotherResponse = this.groomMotherMapper.convert(groom.getGroomMother());
        var groomPreviousPartnerResponse = this.previousPartnerMapper.convert(groom.getPreviousPartner());
        var marriageResponse = this.marriageMapper.convert(marriage);

        return ApplicationMarriageResponse
            .builder()
            .applicationId(application.getId())
            .status(application.getStatus())
            .processId(application.getProcessId())
            .bride(brideResponse)
            .brideFather(brideFatherResponse)
            .brideMother(brideMotherResponse)
            .previousBridePartner(bridePreviousPartnerResponse)
            .guardian(brideGuardianResponse)
            .groom(groomResponse)
            .groomFather(groomFatherResponse)
            .groomMother(groomMotherResponse)
            .previousGroomPartner(groomPreviousPartnerResponse)
            .marriage(marriageResponse)
            .build();
    }
}
