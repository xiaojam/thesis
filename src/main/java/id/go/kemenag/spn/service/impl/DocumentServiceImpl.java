package id.go.kemenag.spn.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.go.kemenag.spn.config.custom.CustomUserDetails;
import id.go.kemenag.spn.constant.DocumentConstant;
import id.go.kemenag.spn.constant.MarriageConstant;
import id.go.kemenag.spn.dto.document.*;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.document.DocumentConfig;
import id.go.kemenag.spn.entity.document.DocumentTemplate;
import id.go.kemenag.spn.entity.document.GeneratedDocument;
import id.go.kemenag.spn.entity.marriage.Marriage;
import id.go.kemenag.spn.entity.master.Master;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.repository.document.DocumentConfigRepository;
import id.go.kemenag.spn.repository.document.DocumentTemplateRepository;
import id.go.kemenag.spn.repository.document.GeneratedDocumentRepository;
import id.go.kemenag.spn.service.AuthService;
import id.go.kemenag.spn.service.DocumentService;
import id.go.kemenag.spn.service.master.MasterService;
import id.go.kemenag.spn.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private AuthService authService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private DocumentConfigRepository documentConfigRepository;

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Autowired
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public byte[] downloadMarriageDocument(
        Marriage marriage,
        CustomUserDetails user,
        DocumentConstant.BundleMarriageType bundleMarriageType
    ) {

        MarriageDocumentDto globalContext = this.buildMarriageContext(marriage, user);

        if (DocumentConstant.BundleMarriageType.COMPLETE.equals(bundleMarriageType)) {
            return this.processAllDocuments(marriage, user, globalContext);
        }

        var isBride = DocumentConstant.BundleMarriageType.BRIDE_ONLY.equals(bundleMarriageType);
        var isPreviousPartnerDeceased = isPreviousPartnerDeceased(marriage, isBride);

        List<DocumentConstant.DocumentType> documentTypes = this.getMarriageDocumentTypes(
            isPreviousPartnerDeceased,
            isBride
        );

        if  (isBride) {
            return this.processBrideMarriageDocument(marriage, globalContext, documentTypes);
        }

        return this.processGroomMarriageDocument(marriage, globalContext, documentTypes);
    }

    private byte[] processAllDocuments(Marriage marriage, CustomUserDetails user, MarriageDocumentDto globalContext) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {

            var isPreviousPartnerDeceasedBride = isPreviousPartnerDeceased(marriage, Boolean.TRUE);
            List<DocumentConstant.DocumentType> documentBrideTypes = this.getMarriageDocumentTypes(
                isPreviousPartnerDeceasedBride,
                Boolean.TRUE
            );

            this.generateAndAddDocumentsToZip(zos, marriage, globalContext, documentBrideTypes, Boolean.TRUE);

            var isPreviousPartnerDeceasedGroom = isPreviousPartnerDeceased(marriage, Boolean.FALSE);
            List<DocumentConstant.DocumentType> documentGroomTypes = this.getMarriageDocumentTypes(
                isPreviousPartnerDeceasedGroom,
                Boolean.FALSE
            );

            this.generateAndAddDocumentsToZip(zos, marriage, globalContext, documentGroomTypes, Boolean.FALSE);

        } catch (Exception e) {
            log.error("Gagal membuat bundel dokumen lengkap untuk aplikasi {}: {}", marriage.getApplication().getId(), e.getMessage(), e);
            throw new BusinessErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Gagal membuat bundel dokumen");
        }

        return byteArrayOutputStream.toByteArray();
    }

    private boolean isPreviousPartnerDeceased(Marriage marriage, boolean isBride) {
        var bridePreviousPartner = marriage.getBride().getPreviousPartner();
        var groomPreviousPartner = marriage.getGroom().getPreviousPartner();

        return isBride ?
            (bridePreviousPartner != null &&
                (bridePreviousPartner.getDeathPlace() != null || bridePreviousPartner.getDeathDate() != null)
            )
            :
            (groomPreviousPartner != null &&
                (groomPreviousPartner.getDeathPlace() != null || groomPreviousPartner.getDeathDate() != null)
            );
    }

    private MarriageDocumentDto buildMarriageContext(Marriage marriage, CustomUserDetails user) {
        MarriageDocumentDto dto = new MarriageDocumentDto();
        
        var brideDto = this.processMarriageDocumentBrideDto(marriage, marriage.getBride().getPreviousPartner() != null ?
            CommonUtil.buildFullName(
                marriage.getBride().getPreviousPartner().getFirstName(),
                marriage.getBride().getPreviousPartner().getLastName(),
                marriage.getBride().getPreviousPartner().getAlias()
            ) : ""
        );
        var groomDto = this.processMarriageDocumentGroomDto(marriage, marriage.getGroom().getPreviousPartner() != null ?
            CommonUtil.buildFullName(
                marriage.getGroom().getPreviousPartner().getFirstName(),
                marriage.getGroom().getPreviousPartner().getLastName(),
                marriage.getGroom().getPreviousPartner().getAlias()
            ) : ""
        );
        
        var brideFatherDto = this.processMarriageDocumentBrideFatherDto(marriage);
        var brideMotherDto = this.processMarriageDocumentBrideMotherDto(marriage);
        var groomFatherDto = this.processMarriageDocumentGroomFatherDto(marriage);
        var groomMotherDto = this.processMarriageDocumentGroomMotherDto(marriage);
        var guardianDto = this.processMarriageDocumentGuardianDto(marriage);
        var marriageDataDto = this.processMarriageDocumentDataDto(marriage);
        var bridePreviousPartnerDto = this.processMarriageDocumentBridePreviousPartnerDto(marriage);
        var groomPreviousPartnerDto = this.processMarriageDocumentGroomPreviousPartnerDto(marriage);
        var brideDocumentDataDto = this.processBrideDocumentDataDto(marriage, user);
        var groomDocumentDataDto = this.processGroomDocumentDataDto(marriage, user);
        
        dto.setBride(brideDto);
        dto.setGroom(groomDto);
        dto.setBrideFather(brideFatherDto);
        dto.setBrideMother(brideMotherDto);
        dto.setGroomFather(groomFatherDto);
        dto.setGroomMother(groomMotherDto);
        dto.setGuardian(guardianDto);
        dto.setMarriageData(marriageDataDto);
        dto.setBridePreviousPartner(bridePreviousPartnerDto);
        dto.setGroomPreviousPartner(groomPreviousPartnerDto);
        dto.setBrideDocumentData(brideDocumentDataDto);
        dto.setGroomDocumentData(groomDocumentDataDto);
        
        return dto;
    }

    @Override
    @Transactional
    public DocumentConfig findByWorkplaceIdAndServiceType(String workplaceId, DocumentConstant.ServiceType serviceType) {
        return this.documentConfigRepository.findByWorkplace_CodeAndServiceTypeAndDeletedFalse(workplaceId, serviceType).orElse(null);
    }

    private byte[] processBrideMarriageDocument(
        Marriage marriage,
        MarriageDocumentDto globalContext,
        List<DocumentConstant.DocumentType> documentTypes
    ) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
            return this.generateZipBundleInternal(marriage, globalContext, documentTypes, Boolean.TRUE);
        } catch (Exception e) {
            throw new BusinessErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Failed to generate document");
        }
    }

    private byte[] processGroomMarriageDocument(
        Marriage marriage,
        MarriageDocumentDto globalContext,
        List<DocumentConstant.DocumentType> documentTypes
    ) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
            return this.generateZipBundleInternal(marriage, globalContext, documentTypes, Boolean.FALSE);
        } catch (Exception e) {
            throw new BusinessErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Failed to generate document");
        }
    }

    private MarriageDocumentBrideDto processMarriageDocumentBrideDto(Marriage marriage, String bridePreviousPartnerFullName) {
        MarriageDocumentBrideDto brideDto = new MarriageDocumentBrideDto();

        var bride = marriage.getBride();

        var brideFullName = CommonUtil.buildFullName(bride.getFirstName(), bride.getLastName(), bride.getAlias());

        var brideAddress = CommonUtil.buildFullAddress(
            bride.getAddress(),
            bride.getRt(),
            bride.getRw(),
            bride.getSubDistrictName(),
            bride.getDistrictName(),
            bride.getCityName(),
            bride.getProvinceName()
        );

        var brideBrithInfo = CommonUtil.buildBirthInfo(bride.getBirthPlace(), bride.getBirthDate());

        var previousPartner = bride.getPreviousPartner();
        String fatherName = "";
        if (previousPartner != null) {
            fatherName = previousPartner.getFatherName();
        }
        var previousPartnerFullName = CommonUtil.buildFullNameWithFatherName(
            bridePreviousPartnerFullName, fatherName, Boolean.TRUE
        );

        brideDto.setPreviousPartnerName(previousPartnerFullName);
        brideDto.setFullName(brideFullName);
        brideDto.setIdentityId(bride.getIdentityId());
        brideDto.setGender(CommonUtil.normalizeGender(bride.getGender()));
        brideDto.setBirth(brideBrithInfo);
        brideDto.setMaritalStatus(CommonUtil.normalizeMaritalStatus(bride.getMaritalStatus(), Boolean.FALSE));
        brideDto.setJob(bride.getJob());
        brideDto.setNationality(bride.getNationality());
        brideDto.setReligion(CommonUtil.normalizeReligion(bride.getReligion()));
        brideDto.setAddress(brideAddress);
        brideDto.setSignName(CommonUtil.simplifiedName(bride.getFirstName(), bride.getLastName()));

        return brideDto;
    }

    private MarriageDocumentGroomDto processMarriageDocumentGroomDto(Marriage marriage, String groomPreviousPartnerFullName) {
        MarriageDocumentGroomDto groomDto = new MarriageDocumentGroomDto();

        var groom = marriage.getGroom();

        var groomFullName = CommonUtil.buildFullName(groom.getFirstName(), groom.getLastName(), groom.getAlias());

        var groomAddress = CommonUtil.buildFullAddress(
            groom.getAddress(),
            groom.getRt(),
            groom.getRw(),
            groom.getSubDistrictName(),
            groom.getDistrictName(),
            groom.getCityName(),
            groom.getProvinceName()
        );

        var groomBrithInfo = CommonUtil.buildBirthInfo(groom.getBirthPlace(), groom.getBirthDate());

        var previousPartner = groom.getPreviousPartner();
        String fatherName = "";
        if (previousPartner != null) {
            fatherName = previousPartner.getFatherName();
        }

        var previousPartnerFullName = CommonUtil.buildFullNameWithFatherName(
            groomPreviousPartnerFullName, fatherName, Boolean.TRUE
        );

        groomDto.setPreviousPartnerName(previousPartnerFullName);
        groomDto.setFullName(groomFullName);
        groomDto.setIdentityId(groom.getIdentityId());
        groomDto.setGender(CommonUtil.normalizeGender(groom.getGender()));
        groomDto.setBirth(groomBrithInfo);
        groomDto.setMaritalStatus(CommonUtil.normalizeMaritalStatus(groom.getMaritalStatus(), Boolean.TRUE));
        groomDto.setJob(groom.getJob());
        groomDto.setNationality(groom.getNationality());
        groomDto.setReligion(CommonUtil.normalizeReligion(groom.getReligion()));
        groomDto.setAddress(groomAddress);
        groomDto.setSignName(CommonUtil.simplifiedName(groom.getFirstName(), groom.getLastName()));

        return groomDto;
    }

    private MarriageDocumentBrideFatherDto processMarriageDocumentBrideFatherDto(Marriage marriage) {
        MarriageDocumentBrideFatherDto brideFatherDto = new MarriageDocumentBrideFatherDto();

        var brideFather = marriage.getBride().getBrideFather();

        var brideFatherFullName = CommonUtil.buildFullName(brideFather.getFirstName(), brideFather.getLastName(), brideFather.getAlias());

        var brideFatherAddress = CommonUtil.buildFullAddress(
            brideFather.getAddress(),
            brideFather.getRt(),
            brideFather.getRw(),
            brideFather.getSubDistrictName(),
            brideFather.getDistrictName(),
            brideFather.getCityName(),
            brideFather.getProvinceName()
        );

        var brideFatherBrithInfo = CommonUtil.buildBirthInfo(brideFather.getBirthPlace(), brideFather.getBirthDate());

        brideFatherDto.setFullName(brideFatherFullName);
        brideFatherDto.setFatherName(brideFather.getFatherName());
        brideFatherDto.setIdentityId(brideFather.getIdentityId());
        brideFatherDto.setBirth(brideFatherBrithInfo);
        brideFatherDto.setJob(brideFather.getJob());
        brideFatherDto.setNationality(brideFather.getNationality());
        brideFatherDto.setReligion(CommonUtil.normalizeReligion(brideFather.getReligion()));
        brideFatherDto.setAddress(brideFatherAddress);
        brideFatherDto.setSignName(CommonUtil.simplifiedName(brideFather.getFirstName(), brideFather.getLastName()));

        return brideFatherDto;
    }

    private MarriageDocumentBrideMotherDto processMarriageDocumentBrideMotherDto(Marriage marriage) {
        MarriageDocumentBrideMotherDto brideMotherDto = new MarriageDocumentBrideMotherDto();

        var brideMother = marriage.getBride().getBrideMother();

        var brideMotherFullName = CommonUtil.buildFullName(brideMother.getFirstName(), brideMother.getLastName(), brideMother.getAlias());

        var brideMotherAddress = CommonUtil.buildFullAddress(
            brideMother.getAddress(),
            brideMother.getRt(),
            brideMother.getRw(),
            brideMother.getSubDistrictName(),
            brideMother.getDistrictName(),
            brideMother.getCityName(),
            brideMother.getProvinceName()
        );

        var brideMotherBrithInfo = CommonUtil.buildBirthInfo(brideMother.getBirthPlace(), brideMother.getBirthDate());

        brideMotherDto.setFullName(brideMotherFullName);
        brideMotherDto.setFatherName(brideMother.getFatherName());
        brideMotherDto.setIdentityId(brideMother.getIdentityId());
        brideMotherDto.setBirth(brideMotherBrithInfo);
        brideMotherDto.setJob(brideMother.getJob());
        brideMotherDto.setNationality(brideMother.getNationality());
        brideMotherDto.setReligion(CommonUtil.normalizeReligion(brideMother.getReligion()));
        brideMotherDto.setAddress(brideMotherAddress);
        brideMotherDto.setSignName(CommonUtil.simplifiedName(brideMother.getFirstName(), brideMother.getLastName()));

        return brideMotherDto;
    }

    private MarriageDocumentGroomFatherDto processMarriageDocumentGroomFatherDto(Marriage marriage) {
        MarriageDocumentGroomFatherDto groomFatherDto = new MarriageDocumentGroomFatherDto();

        var groomFather = marriage.getGroom().getGroomFather();

        var groomFatherFullName = CommonUtil.buildFullName(groomFather.getFirstName(), groomFather.getLastName(), groomFather.getAlias());

        var groomFatherAddress = CommonUtil.buildFullAddress(
            groomFather.getAddress(),
            groomFather.getRt(),
            groomFather.getRw(),
            groomFather.getSubDistrictName(),
            groomFather.getDistrictName(),
            groomFather.getCityName(),
            groomFather.getProvinceName()
        );

        var groomFatherBrithInfo = CommonUtil.buildBirthInfo(groomFather.getBirthPlace(), groomFather.getBirthDate());

        groomFatherDto.setFullName(groomFatherFullName);
        groomFatherDto.setFatherName(groomFather.getFatherName());
        groomFatherDto.setIdentityId(groomFather.getIdentityId());
        groomFatherDto.setBirth(groomFatherBrithInfo);
        groomFatherDto.setJob(groomFather.getJob());
        groomFatherDto.setNationality(groomFather.getNationality());
        groomFatherDto.setReligion(CommonUtil.normalizeReligion(groomFather.getReligion()));
        groomFatherDto.setAddress(groomFatherAddress);
        groomFatherDto.setSignName(CommonUtil.simplifiedName(groomFather.getFirstName(), groomFather.getLastName()));

        return groomFatherDto;
    }

    private MarriageDocumentGroomMotherDto processMarriageDocumentGroomMotherDto(Marriage marriage) {
        MarriageDocumentGroomMotherDto groomMotherDto = new MarriageDocumentGroomMotherDto();

        var groomMother = marriage.getGroom().getGroomMother();

        var groomMotherFullName = CommonUtil.buildFullName(groomMother.getFirstName(), groomMother.getLastName(), groomMother.getAlias());

        var groomMotherAddress = CommonUtil.buildFullAddress(
            groomMother.getAddress(),
            groomMother.getRt(),
            groomMother.getRw(),
            groomMother.getSubDistrictName(),
            groomMother.getDistrictName(),
            groomMother.getCityName(),
            groomMother.getProvinceName()
        );

        var groomMotherBrithInfo = CommonUtil.buildBirthInfo(groomMother.getBirthPlace(), groomMother.getBirthDate());

        groomMotherDto.setFullName(groomMotherFullName);
        groomMotherDto.setFatherName(groomMother.getFatherName());
        groomMotherDto.setIdentityId(groomMother.getIdentityId());
        groomMotherDto.setBirth(groomMotherBrithInfo);
        groomMotherDto.setJob(groomMother.getJob());
        groomMotherDto.setNationality(groomMother.getNationality());
        groomMotherDto.setReligion(CommonUtil.normalizeReligion(groomMother.getReligion()));
        groomMotherDto.setAddress(groomMotherAddress);
        groomMotherDto.setSignName(CommonUtil.simplifiedName(groomMother.getFirstName(), groomMother.getLastName()));

        return groomMotherDto;
    }

    private MarriageDocumentDataDto processMarriageDocumentDataDto(Marriage marriage) {
        MarriageDocumentDataDto marriageDataDto = new MarriageDocumentDataDto();

        var marriageAddress = CommonUtil.buildFullAddress(
            marriage.getAddress(),
            marriage.getRt(),
            marriage.getRw(),
            marriage.getSubDistrictName(),
            marriage.getDistrictName(),
            marriage.getCityName(),
            marriage.getProvinceName()
        );

        marriageDataDto.setDate(CommonUtil.normalizeDateTime(marriage.getDatetime()));
        marriageDataDto.setDowry(marriage.getDowry());
        marriageDataDto.setPlace(marriageAddress);

        return marriageDataDto;
    }

    private MarriageDocumentGuardianDto processMarriageDocumentGuardianDto(Marriage marriage) {
        MarriageDocumentGuardianDto guardianDto = new MarriageDocumentGuardianDto();

        var guardian = marriage.getBride().getGuardian();

        var guardianFullName = CommonUtil.buildFullName(guardian.getFirstName(), guardian.getLastName(), guardian.getAlias());

        var guardianAddress = CommonUtil.buildFullAddress(
            guardian.getAddress(),
            guardian.getRt(),
            guardian.getRw(),
            guardian.getSubDistrictName(),
            guardian.getDistrictName(),
            guardian.getCityName(),
            guardian.getProvinceName()
        );

        var guardianBrithInfo = CommonUtil.buildBirthInfo(guardian.getBirthPlace(), guardian.getBirthDate());

        guardianDto.setStatus(CommonUtil.normalizeGuardianStatus(guardian.getStatus()));
        guardianDto.setFullName(guardianFullName);
        guardianDto.setFatherName(guardian.getFatherName());
        guardianDto.setIdentityId(guardian.getIdentityId());
        guardianDto.setBirth(guardianBrithInfo);
        guardianDto.setJob(guardian.getJob());
        guardianDto.setNationality(guardian.getNationality());
        guardianDto.setReligion(CommonUtil.normalizeReligion(guardian.getReligion()));
        guardianDto.setAddress(guardianAddress);
        guardianDto.setReason(CommonUtil.getNormalizeReason(guardian.getStatus(), marriage.getBride().getBrideFather().isDeceased()));
        guardianDto.setRelationship(CommonUtil.getNormalizeGuardianType(guardian.getRelationship()));

        return guardianDto;
    }

    private MarriageDocumentBridePreviousPartnerDto processMarriageDocumentBridePreviousPartnerDto(Marriage marriage) {
        MarriageDocumentBridePreviousPartnerDto bridePreviousPartnerDto = new MarriageDocumentBridePreviousPartnerDto();

        var bridePreviousPartner = marriage.getBride().getPreviousPartner();

        if (bridePreviousPartner == null) {
            return bridePreviousPartnerDto;
        }

        var bridePreviousPartnerFullName = CommonUtil.buildFullName(
            bridePreviousPartner.getFirstName(),
            bridePreviousPartner.getLastName(),
            bridePreviousPartner.getAlias()
        );

        var bridePreviousPartnerAddress = CommonUtil.buildFullAddress(
            bridePreviousPartner.getAddress(),
            bridePreviousPartner.getRt(),
            bridePreviousPartner.getRw(),
            bridePreviousPartner.getSubDistrictName(),
            bridePreviousPartner.getDistrictName(),
            bridePreviousPartner.getCityName(),
            bridePreviousPartner.getProvinceName()
        );

        var bridePreviousPartnerBrithInfo = CommonUtil.buildBirthInfo(bridePreviousPartner.getBirthPlace(), bridePreviousPartner.getBirthDate());

        bridePreviousPartnerDto.setFullName(bridePreviousPartnerFullName);
        bridePreviousPartnerDto.setIdentityId(bridePreviousPartner.getIdentityId());
        bridePreviousPartnerDto.setFatherName(bridePreviousPartner.getFatherName());
        bridePreviousPartnerDto.setBirth(bridePreviousPartnerBrithInfo);
        bridePreviousPartnerDto.setDeathDate(CommonUtil.normalizeDate(bridePreviousPartner.getDeathDate()));
        bridePreviousPartnerDto.setDeathPlace(bridePreviousPartner.getDeathPlace());
        bridePreviousPartnerDto.setJob(bridePreviousPartner.getJob());
        bridePreviousPartnerDto.setNationality(bridePreviousPartner.getNationality());
        bridePreviousPartnerDto.setReligion(CommonUtil.normalizeReligion(bridePreviousPartner.getReligion()));
        bridePreviousPartnerDto.setAddress(bridePreviousPartnerAddress);

        return bridePreviousPartnerDto;
    }

    private MarriageDocumentGroomPreviousPartnerDto processMarriageDocumentGroomPreviousPartnerDto(Marriage marriage) {
        MarriageDocumentGroomPreviousPartnerDto groomPreviousPartnerDto = new MarriageDocumentGroomPreviousPartnerDto();

        var groomPreviousPartner = marriage.getGroom().getPreviousPartner();

        if (groomPreviousPartner == null) {
            return groomPreviousPartnerDto;
        }

        var groomPreviousPartnerFullName = CommonUtil.buildFullName(
            groomPreviousPartner.getFirstName(),
            groomPreviousPartner.getLastName(),
            groomPreviousPartner.getAlias()
        );

        var groomPreviousPartnerAddress = CommonUtil.buildFullAddress(
            groomPreviousPartner.getAddress(),
            groomPreviousPartner.getRt(),
            groomPreviousPartner.getRw(),
            groomPreviousPartner.getSubDistrictName(),
            groomPreviousPartner.getDistrictName(),
            groomPreviousPartner.getCityName(),
            groomPreviousPartner.getProvinceName()
        );

        var groomPreviousPartnerBrithInfo = CommonUtil.buildBirthInfo(groomPreviousPartner.getBirthPlace(), groomPreviousPartner.getBirthDate());

        groomPreviousPartnerDto.setFullName(groomPreviousPartnerFullName);
        groomPreviousPartnerDto.setIdentityId(groomPreviousPartner.getIdentityId());
        groomPreviousPartnerDto.setFatherName(groomPreviousPartner.getFatherName());
        groomPreviousPartnerDto.setBirth(groomPreviousPartnerBrithInfo);
        groomPreviousPartnerDto.setDeathDate(CommonUtil.normalizeDate(groomPreviousPartner.getDeathDate()));
        groomPreviousPartnerDto.setDeathPlace(groomPreviousPartner.getDeathPlace());
        groomPreviousPartnerDto.setJob(groomPreviousPartner.getJob());
        groomPreviousPartnerDto.setNationality(groomPreviousPartner.getNationality());
        groomPreviousPartnerDto.setReligion(CommonUtil.normalizeReligion(groomPreviousPartner.getReligion()));
        groomPreviousPartnerDto.setAddress(groomPreviousPartnerAddress);

        return groomPreviousPartnerDto;
    }

    private GroomDocumentDataDto processGroomDocumentDataDto(Marriage marriage, CustomUserDetails user) {
        GroomDocumentDataDto groomDocumentDataDto = new GroomDocumentDataDto();

        var groom = marriage.getGroom();

        var data = this.masterService.findByGroupNameAndCode("WILAYAH", user.getWorkplaceCode());

        if (data == null) {
            data = this.masterService.findByGroupNameAndCode(
                "WILAYAH",
                groom.getSubDistrictCode()
            );
        }

        if (data == null) {
            data = Master.builder().code(groom.getSubDistrictCode()).name(groom.getSubDistrictName()).build();
        }

        var documentConfig = this.findByWorkplaceIdAndServiceType(data.getCode(), DocumentConstant.ServiceType.MARRIAGE);
        var villageHeadName = documentConfig != null ? documentConfig.getHeadName() : "";
        var villageInfo = documentConfig != null ? documentConfig.getWorkplace().getName() : "";
        var generatedNumber = this.generateNextDocumentNumber(documentConfig);

        groomDocumentDataDto.setDocumentNumber(generatedNumber);
        groomDocumentDataDto.setSubDistrictName(groom.getSubDistrictName());
        groomDocumentDataDto.setDistrictName(groom.getDistrictName());
        groomDocumentDataDto.setCityName(groom.getCityName());
        groomDocumentDataDto.setHeadVillageName(villageHeadName);
        groomDocumentDataDto.setCreatedDate(
            CommonUtil.buildSignatureInfo(villageInfo, LocalDate.now())
        );

        return groomDocumentDataDto;
    }

    private BrideDocumentDataDto processBrideDocumentDataDto(Marriage marriage, CustomUserDetails user) {
        BrideDocumentDataDto brideDocumentDataDto = new BrideDocumentDataDto();

        var bride = marriage.getBride();

        var data = this.masterService.findByGroupNameAndCode("WILAYAH", user.getWorkplaceCode());

        if (data == null) {
            data = this.masterService.findByGroupNameAndCode(
                "WILAYAH",
                bride.getSubDistrictCode()
            );
        }

        if (data == null) {
            data = Master.builder().code(bride.getSubDistrictCode()).name(bride.getSubDistrictName()).build();
        }

        var config = this.findByWorkplaceIdAndServiceType(data.getCode(), DocumentConstant.ServiceType.MARRIAGE);
        var villageHeadName = config != null ? config.getHeadName() : "";
        var villageInfo = config != null ? config.getWorkplace().getName() : "";

        var generatedNumber = this.generateNextDocumentNumber(config);

        brideDocumentDataDto.setDocumentNumber(generatedNumber);
        brideDocumentDataDto.setSubDistrictName(bride.getSubDistrictName());
        brideDocumentDataDto.setDistrictName(bride.getDistrictName());
        brideDocumentDataDto.setCityName(bride.getCityName());
        brideDocumentDataDto.setHeadVillageName(villageHeadName);
        brideDocumentDataDto.setCreatedDate(
            CommonUtil.buildSignatureInfo(villageInfo, LocalDate.now())
        );

        return brideDocumentDataDto;
    }

    private List<DocumentConstant.DocumentType> getMarriageDocumentTypes(
        Boolean isPreviousPartnerDeceased,
        Boolean isBride
    ) {
        List<DocumentConstant.DocumentType> documentTypes = new ArrayList<>();

        if (isBride) {
            if (isPreviousPartnerDeceased) {
                documentTypes.add(DocumentConstant.DocumentType.N6_BRIDE);
            }

            documentTypes.addAll(
                List.of(
                    DocumentConstant.DocumentType.N1_BRIDE,
                    DocumentConstant.DocumentType.N2_BRIDE,
                    DocumentConstant.DocumentType.N4_BRIDE,
                    DocumentConstant.DocumentType.N5_BRIDE,
                    DocumentConstant.DocumentType.WN
                )
            );

            return documentTypes;
        }

        if (isPreviousPartnerDeceased) {
            documentTypes.add(DocumentConstant.DocumentType.N6_GROOM);
        }

        documentTypes.addAll(
            List.of(
                DocumentConstant.DocumentType.N1_GROOM,
                DocumentConstant.DocumentType.N2_GROOM,
                DocumentConstant.DocumentType.N4_GROOM,
                DocumentConstant.DocumentType.N5_GROOM
            )
        );

        return documentTypes;
    }

    private DocumentTemplate findTemplateByType(DocumentConstant.DocumentType documentType) {
        return this.documentTemplateRepository.findByDocumentTypeAndDeletedFalse(documentType).orElse(null);
    }

    private GeneratedDocument save(GeneratedDocument generatedDocument) {
        return this.generatedDocumentRepository.save(generatedDocument);
    }

    @Transactional
    private byte[] generateZipBundleInternal(
        Marriage marriage,
        MarriageDocumentDto globalContext,
        List<DocumentConstant.DocumentType> documentTypesToProcess,
        Boolean isBride
    ) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
            this.generateAndAddDocumentsToZip(zos, marriage, globalContext, documentTypesToProcess, isBride);
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Transactional
    private void generateAndAddDocumentsToZip(
        ZipOutputStream zos,
        Marriage marriage,
        MarriageDocumentDto globalContext,
        List<DocumentConstant.DocumentType> documentTypesToProcess,
        Boolean isBride
    ) throws IOException {

        for (DocumentConstant.DocumentType docType : documentTypesToProcess) {
            log.info("Membuat tipe dokumen: {} untuk aplikasi {}", docType, marriage.getApplication().getId());

            var template = this.findTemplateByType(docType);
            if (template == null) {
                throw new BusinessErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Template tidak ditemukan untuk tipe dokumen: " + docType);
            }

            DocumentConfig config = template.getConfig();
            if (config == null || config.isDeleted()) {
                throw new BusinessErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Konfigurasi aktif tidak ditemukan untuk template: " + template.getName());
            }

            Context thymeleafContext = new Context();
            thymeleafContext.setVariable("ctx", globalContext);

            byte[] pdfBytes = generatePdfFromTemplate(template.getFilePath(), thymeleafContext);

            String pdfFileName = generatePdfFilename(docType, marriage);
            addFileToZip(zos, pdfFileName, pdfBytes);
            String documentNumber = null;

            if (isBride) {
                documentNumber = globalContext.getBrideDocumentData().getDocumentNumber();
            } else {
                documentNumber = globalContext.getGroomDocumentData().getDocumentNumber();
            }

            saveGeneratedDocumentMetadata(marriage.getApplication(), template, documentNumber, pdfFileName, globalContext, LocalDate.now());
        }
    }

    private void addFileToZip(ZipOutputStream zos, String fileName, byte[] data) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);
        zos.write(data);
        zos.closeEntry();
    }

    private String generatePdfFilename(DocumentConstant.DocumentType docType, Marriage marriage) {
        String applicationNumber = "unknown";
        if (marriage.getApplication() != null && marriage.getApplication().getApplicationNumber() != null) {
            applicationNumber = marriage.getApplication().getApplicationNumber();
        }

        var docName = docType.name() + "_app-" + applicationNumber + ".pdf";
        return docName.toUpperCase();
    }

    @Transactional
    private void saveGeneratedDocumentMetadata(
        Application application,
        DocumentTemplate template,
        String documentNumber,
        String generatedFilePath,
        MarriageDocumentDto dataUsed,
        LocalDate issueDate
    ) {
        try {
            String dataSnapshotJson = this.objectMapper.writeValueAsString(dataUsed);

            GeneratedDocument generatedDoc = GeneratedDocument
                .builder()
                .application(application)
                .documentTemplate(template)
                .documentNumber(documentNumber)
                .filePath(generatedFilePath)
                .dataSnapshot(dataSnapshotJson)
                .issuedAt(issueDate.atStartOfDay())
                .build();

            this.save(generatedDoc);
            log.info("Saved metadata for generated document: {}", documentNumber);
        } catch (Exception e) {
            log.error("Failed to save metadata for generated document {}: {}", documentNumber, e.getMessage(), e);
        }
    }

    public String generateNextDocumentNumber(DocumentConfig config) {
        if (config == null || config.getId() == null) {
            log.warn("DocumentConfig is null or has no ID, cannot generate document number.");
            return "-";
        }

        DocumentConfig lockedConfig = this.findByIdWithLock(config.getId());
        if (lockedConfig == null) {
            log.warn("Locked DocumentConfig not found for ID: {}", config.getId());
            return "-";
        }

        Integer nextSequence = lockedConfig.getLastSequence() + 1;
        lockedConfig.setLastSequence(nextSequence);

        String format = lockedConfig.getNumberingFormat();
        LocalDate today = LocalDate.now();

        return format
            .replace("{{SEQ}}", String.valueOf(nextSequence))
            .replace("{{SEQ_3}}", String.format("%03d", nextSequence))
            .replace("{{SEQ_4}}", String.format("%04d", nextSequence))
            .replace("{{YYYY}}", String.valueOf(today.getYear()))
            .replace("{{YY}}", String.valueOf(today.getYear()).substring(2))
            .replace("{{MM}}", String.format("%02d", today.getMonthValue()))
            .replace("{{ROMAN_MONTH}}", CommonUtil.getRomanMonth(today.getMonthValue()));
    }

    private DocumentConfig save(DocumentConfig documentConfig) {
        return this.documentConfigRepository.save(documentConfig);
    }

    private DocumentConfig findByIdWithLock(UUID id) {
        return this.documentConfigRepository.findByIdWithLock(id).orElse(null);
    }

    private byte[] generatePdfFromTemplate(String templatePath, Context context) throws IOException {
        log.debug("Processing template: {}", templatePath);
        String processedHtml = templateEngine.process(templatePath, context);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(processedHtml, "classpath:/templates/");
            builder.toStream(outputStream);
            builder.run();
            log.debug("PDF generated successfully from template: {}", templatePath);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error converting HTML to PDF for template {}: {}", templatePath, e.getMessage());
            throw new IOException("PDF generation failed for template: " + templatePath, e);
        }
    }
}
