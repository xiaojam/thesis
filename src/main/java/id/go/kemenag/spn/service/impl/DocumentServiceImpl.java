package id.go.kemenag.spn.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.go.kemenag.spn.config.custom.CustomUserDetails;
import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.constant.DocumentConstant;
import id.go.kemenag.spn.constant.FormatterConstant;
import id.go.kemenag.spn.dto.document.divorce.*;
import id.go.kemenag.spn.dto.document.marriage.*;
import id.go.kemenag.spn.dto.document.updatehistory.UpdateHistoryDto;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.UpdateHistory;
import id.go.kemenag.spn.entity.divorce.*;
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
import id.go.kemenag.spn.service.UpdateHistoryService;
import id.go.kemenag.spn.service.master.MasterService;
import id.go.kemenag.spn.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
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

    @Autowired
    private UpdateHistoryService updateHistoryService;

    private static final Locale INDONESIA = new Locale("id", "ID");

    @Override
    @Transactional
    public byte[] downloadMarriageDocument(
        Marriage marriage,
        CustomUserDetails user,
        DocumentConstant.BundleMarriageType bundleMarriageType
    ) {
        Set<String> alreadyAddedFiles = new HashSet<>();

        MarriageDocumentDto globalContext = this.buildMarriageContext(marriage, user);

        if (DocumentConstant.BundleMarriageType.COMPLETE.equals(bundleMarriageType)) {
            return this.processAllDocuments(marriage, globalContext, user.getWorkplaceCode(), alreadyAddedFiles);
        }

        var isBride = DocumentConstant.BundleMarriageType.BRIDE_ONLY.equals(bundleMarriageType);
        var isPreviousPartnerDeceased = isPreviousPartnerDeceased(marriage, isBride);

        List<DocumentConstant.DocumentType> documentTypes = this.getMarriageDocumentTypes(
            isPreviousPartnerDeceased,
            isBride
        );

        if  (isBride) {
            return this.processBrideMarriageDocument(
                marriage,
                globalContext,
                documentTypes,
                user.getWorkplaceCode(),
                alreadyAddedFiles
            );
        }

        return this.processGroomMarriageDocument(
            marriage,
            globalContext,
            documentTypes,
            user.getWorkplaceCode(),
            alreadyAddedFiles
        );
    }

    private byte[] processAllDocuments(
        Marriage marriage,
        MarriageDocumentDto globalContext,
        String workplaceId,
        Set<String> alreadyAddedFiles
    ) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {

            var isPreviousPartnerDeceasedGroom = isPreviousPartnerDeceased(marriage, Boolean.FALSE);
            List<DocumentConstant.DocumentType> documentGroomTypes = this.getMarriageDocumentTypes(
                isPreviousPartnerDeceasedGroom,
                Boolean.FALSE
            );

            this.generateAndAddDocumentsToZip(
                zos,
                marriage,
                globalContext,
                documentGroomTypes,
                Boolean.FALSE,
                workplaceId,
                alreadyAddedFiles
            );

            var isPreviousPartnerDeceasedBride = isPreviousPartnerDeceased(marriage, Boolean.TRUE);
            List<DocumentConstant.DocumentType> documentBrideTypes = this.getMarriageDocumentTypes(
                isPreviousPartnerDeceasedBride,
                Boolean.TRUE
            );

            this.generateAndAddDocumentsToZip(
                zos,
                marriage,
                globalContext,
                documentBrideTypes,
                Boolean.TRUE,
                workplaceId,
                alreadyAddedFiles
            );

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
        var histories = this.processHistoriesDto(marriage.getApplication().getId());
        
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
        dto.setHistories(histories);
        
        return dto;
    }

    private List<UpdateHistoryDto> processHistoriesDto(UUID applicationId) {
        List<UpdateHistoryDto> histories = new ArrayList<>();

        var updateHistories = this.updateHistoryService.findAllByApplicationId(applicationId);
        if (updateHistories == null || updateHistories.isEmpty()) {
            return histories;
        }

        updateHistories.forEach(
            u -> {
                UpdateHistoryDto dto = new UpdateHistoryDto();
                dto.setLabel(CommonUtil.normalizeDocumentLabel(u.getLabel()));
                dto.setOldValue(u.getOldValue());
                dto.setNewValue(u.getNewValue());
                dto.setTime(CommonUtil.normalizeZonedDateTime(u.getCreatedAt()));
                dto.setHandler(u.getUpdatedBy());

                histories.add(dto);
            }
        );

        return histories;
    }

    @Override
    @Transactional
    public DocumentConfig findByWorkplaceIdAndServiceType(String workplaceId, DocumentConstant.ServiceType serviceType) {
        return this.documentConfigRepository.findByWorkplace_CodeAndServiceTypeAndDeletedFalse(workplaceId, serviceType).orElse(null);
    }

    @Override
    public byte[] downloadDivorceDocument(DivorceCase divorceCase) {
        DivorceDocumentDto documentDto = this.buildDocumentDto(divorceCase);

        Context context = new Context(INDONESIA);
        context.setVariable("documentDto", documentDto);

        DocumentConstant.DocumentType docType = this.mapCaseTypeToDocumentType(divorceCase.getCaseType());
        DocumentConstant.ServiceType serviceType = DocumentConstant.ServiceType.DIVORCE;
        String workplaceCode = divorceCase.getCourtCode();

        DocumentTemplate template = this.findTemplateByContext(docType, serviceType, workplaceCode);

        String templateName = template.getFilePath();

        String processedHtml = templateEngine.process(templateName, context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(processedHtml, "file:///");
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat dokumen PDF.", e);
        }
    }

    private DocumentConstant.DocumentType mapCaseTypeToDocumentType(DivorceConstant.CaseType caseType) {
        if (caseType == null) {
            return DocumentConstant.DocumentType.BASIC;
        }

        return switch (caseType) {
            case COMPLETE -> DocumentConstant.DocumentType.COMPLETE;
            case PROPERTY -> DocumentConstant.DocumentType.PROPERTY;
            case CHILD_CUSTODY -> DocumentConstant.DocumentType.CHILD_CUSTODY;
            default -> DocumentConstant.DocumentType.BASIC;
        };
    }

    private DivorceDocumentDto buildDocumentDto(DivorceCase divorceCase) {
        DivorceDocumentDto dto = new DivorceDocumentDto();

        dto.setCaseType(divorceCase.getCaseType().name());
        dto.setCaseTitle(formatCaseTitle(divorceCase.getCaseType()));
        dto.setCourtName(divorceCase.getCourtName());
        dto.setCourtCity(divorceCase.getCourtName());

        dto.setPlaintiff(processPlaintiffDto(divorceCase.getPlaintiff()));
        dto.setDefendant(processDefendantDto(divorceCase.getDefendant()));

        dto.setMarriageData(processMarriageDto(divorceCase.getMarriageData()));
        dto.setDivorceReason(processReasonDto(divorceCase.getDivorceReason(), divorceCase.getReconciliationAttemptDescription()));

        dto.setIddahSupportAmount(CommonUtil.formatCurrency(divorceCase.getIddahSupportAmount()));
        dto.setMutahDescription(divorceCase.getMutahDescription());
        dto.setMaddiyahSupportAmount(CommonUtil.formatCurrency(divorceCase.getMaddiyahSupportAmount()));
        dto.setMaddiyahDurationInMonths(formatDuration(divorceCase.getMaddiyahDurationInMonths()));

        dto.setChildClaim(processChildClaimDto(divorceCase.getChildClaim()));
        dto.setPropertyClaim(processPropertyClaimDto(divorceCase.getPropertyClaim()));

        return dto;
    }


    private DivorceDocumentPlaintiffDto processPlaintiffDto(Plaintiff p) {
        if (p == null) return null;
        DivorceDocumentPlaintiffDto dto = new DivorceDocumentPlaintiffDto();

        var fullName = p.getFirstName() + " " + p.getLastName();
        var isMale = DivorceConstant.Gender.MALE.equals(p.getGender());
        var fullAddress = CommonUtil.buildFullAddress(
            p.getAddress(),
            p.getRt(),
            p.getRw(),
            p.getSubDistrictName(),
            p.getDistrictName(),
            p.getCityName(),
            p.getProvinceName(),
            p.getZipCode()
        );

        dto.setFullName(CommonUtil.buildFullNameWithFatherName(fullName, p.getFatherName(), isMale));
        dto.setIdentityId(p.getIdentityNumber());
        dto.setBirth(CommonUtil.buildBirthInfo(p.getBirthPlace(), p.getBirthDate()));
        dto.setGender(CommonUtil.normalizeGender(p.getGender()));
        dto.setReligion(CommonUtil.normalizeReligion(p.getReligion()));
        dto.setEducation(p.getEducation());
        dto.setJob(p.getJob());
        dto.setSalary(CommonUtil.formatCurrency(p.getSalary()));
        dto.setPhoneNumber(p.getPhoneNumber());
        dto.setAddress(fullAddress);
        dto.setSignName(CommonUtil.simplifiedName(p.getFirstName(), p.getLastName()));

        return dto;
    }

    private DivorceDocumentDefendantDto processDefendantDto(Defendant d) {
        if (d == null) return null;
        DivorceDocumentDefendantDto dto = new DivorceDocumentDefendantDto();

        var fullName = d.getFirstName() + " " + d.getLastName();
        var isMale = DivorceConstant.Gender.MALE.equals(d.getGender());

        var fullAddress = CommonUtil.buildFullAddress(
            d.getAddress(),
            d.getRt(),
            d.getRw(),
            d.getSubDistrictName(),
            d.getDistrictName(),
            d.getCityName(),
            d.getProvinceName(),
            d.getZipCode()
        );

        dto.setFullName(CommonUtil.buildFullNameWithFatherName(fullName, d.getFatherName(), isMale));
        dto.setIdentityId(d.getIdentityNumber());
        dto.setBirth(CommonUtil.buildBirthInfo(d.getBirthPlace(), d.getBirthDate()));
        dto.setGender(CommonUtil.normalizeGender(d.getGender()));
        dto.setReligion(CommonUtil.normalizeReligion(d.getReligion()));
        dto.setJob(d.getJob());
        dto.setSalary(CommonUtil.formatCurrency(d.getSalary()));
        dto.setEducation(d.getEducation());
        dto.setPhoneNumber(d.getPhoneNumber());
        dto.setAddress(fullAddress);

        return dto;
    }

    private DivorceDocumentMarriageDataDto processMarriageDto(MarriageData m) {
        DivorceDocumentMarriageDataDto dto = new DivorceDocumentMarriageDataDto();

        dto.setMarriageDate(CommonUtil.normalizeDate(m.getMarriageDate()));
        dto.setMarriagePlace(m.getMarriagePlace());
        dto.setMarriageCertificateNumber(m.getMarriageCertificateNumber());
        dto.setHouseholdAddress(m.getHouseholdAddress());

        return dto;
    }

    private DivorceDocumentReasonDto processReasonDto(DivorceReason r, String reconciliationAttempt) {
        DivorceDocumentReasonDto dto = new DivorceDocumentReasonDto();

        dto.setConflictStartDate(CommonUtil.normalizeDate(r.getConflictStartDate()));
        dto.setConflictClimaxDate(CommonUtil.normalizeDate(r.getConflictClimaxDate()));
        dto.setSeparationDate(CommonUtil.normalizeDate(r.getSeparationDate()));
        dto.setConflictCauses(r.getConflictCauses() != null ? r.getConflictCauses() : Collections.emptyList());
        dto.setReconciliationAttemptDescription(reconciliationAttempt);
        dto.setTotalSeparationDuration(
            CommonUtil.normalizeTotalDuration(r.getSeparationDate(), r.getCreatedAt().toLocalDate())
        );

        return dto;
    }

    private DivorceDocumentChildClaimDto processChildClaimDto(ChildClaim c) {
        if (c == null || c.getChildren() == null || c.getChildren().isEmpty()) return null;

        DivorceDocumentChildClaimDto dto = new DivorceDocumentChildClaimDto();

        dto.setChildCount(String.valueOf(c.getChildren().size()));
        dto.setMonthlySupport(CommonUtil.formatCurrency(c.getMonthlySupport()));
        dto.setChildren(
            c.getChildren().stream()
                .map(this::processChildDto)
                .collect(Collectors.toList())
        );

        return dto;
    }

    private DivorceDocumentChildrenDto processChildDto(Child c) {
        DivorceDocumentChildrenDto dto = new DivorceDocumentChildrenDto();

        dto.setFullName(c.getName());
        dto.setGender(c.getGender());
        dto.setAge(c.getAge() != null ? c.getAge() + " tahun" : null);

        return dto;
    }

    private DivorceDocumentPropertyClaimDto processPropertyClaimDto(PropertyClaim pc) {
        if (pc == null || pc.getProperties() == null || pc.getProperties().isEmpty()) return null;

        DivorceDocumentPropertyClaimDto dto = new DivorceDocumentPropertyClaimDto();

        dto.setDivisionRequest(pc.getDivisionRequest());
        dto.setProperties(
            pc.getProperties().stream()
                .map(this::processPropertyDto)
                .collect(Collectors.toList())
        );

        return dto;
    }

    private DivorceDocumentPropertiesDto processPropertyDto(SharedProperty sp) {
        DivorceDocumentPropertiesDto dto = new DivorceDocumentPropertiesDto();

        dto.setPropertyType(sp.getPropertyType());
        dto.setDescription(sp.getDescription());
        dto.setOwnershipProof(sp.getOwnershipProof());
        dto.setEstimatedValue(CommonUtil.formatCurrency(sp.getEstimatedValue()));

        return dto;
    }

    private String formatDuration(Integer months) {
        if (months == null) return null;
        return months + " bulan";
    }

    private String formatCaseTitle(DivorceConstant.CaseType caseType) {
        switch (caseType) {
            case BASIC:
                return "GUGATAN PERCERAIAN";
            case CHILD_CUSTODY:
                return "GUGATAN PERCERAIAN, HAK ASUH ANAK, DAN NAFKAH ANAK";
            case PROPERTY:
                return "GUGATAN PERCERAIAN DAN HARTA BERSAMA";
            case COMPLETE:
                return "GUGATAN PERCERAIAN, HAK ASUH ANAK, NAFKAH ANAK, DAN HARTA BERSAMA";
            default:
                return "GUGATAN PERCERAIAN";
        }
    }

    private byte[] processBrideMarriageDocument(
        Marriage marriage,
        MarriageDocumentDto globalContext,
        List<DocumentConstant.DocumentType> documentTypes,
        String userWorkplaceId,
        Set<String> alreadyAddedFiles
    ) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
            return this.generateZipBundleInternal(
                marriage,
                globalContext,
                documentTypes,
                Boolean.TRUE,
                userWorkplaceId,
                alreadyAddedFiles
            );
        } catch (Exception e) {
            throw new BusinessErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Failed to generate document");
        }
    }

    private byte[] processGroomMarriageDocument(
        Marriage marriage,
        MarriageDocumentDto globalContext,
        List<DocumentConstant.DocumentType> documentTypes,
        String userWorkplaceId,
        Set<String> alreadyAddedFiles
    ) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
            return this.generateZipBundleInternal(
                marriage,
                globalContext,
                documentTypes,
                Boolean.FALSE,
                userWorkplaceId,
                alreadyAddedFiles
            );
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
            bride.getProvinceName(),
            bride.getZipCode()
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
            groom.getProvinceName(),
            groom.getZipCode()
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
            brideFather.getProvinceName(),
            brideFather.getZipCode()
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
            brideMother.getProvinceName(),
            brideMother.getZipCode()
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
            groomFather.getProvinceName(),
            groomFather.getZipCode()
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
            groomMother.getProvinceName(),
            groomMother.getZipCode()
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
            marriage.getProvinceName(),
            marriage.getZipCode()
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
            guardian.getProvinceName(),
            guardian.getZipCode()
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
            bridePreviousPartner.getProvinceName(),
            bridePreviousPartner.getZipCode()
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
            groomPreviousPartner.getProvinceName(),
            groomPreviousPartner.getZipCode()
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
        UUID configId = documentConfig != null ? documentConfig.getId() : null;

        // Since data use the same number for all documents, we use N1 to check existing number
        String existingNumber = this.findExistingDocumentNumberByApplication(
            marriage.getApplication().getId(),
            DocumentConstant.DocumentType.N1_GROOM,
            configId
        );
        String generatedNumber = StringUtils.hasText(existingNumber) ? existingNumber : this.generateNextDocumentNumber(documentConfig);

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

        var documentConfig = this.findByWorkplaceIdAndServiceType(data.getCode(), DocumentConstant.ServiceType.MARRIAGE);
        var villageHeadName = documentConfig != null ? documentConfig.getHeadName() : "";
        var villageInfo = documentConfig != null ? documentConfig.getWorkplace().getName() : "";
        var configId = documentConfig != null ? documentConfig.getId() : null;

        // Since data use the same number for all documents, we use N1 to check existing number
        String existingNumber = this.findExistingDocumentNumberByApplication(
            marriage.getApplication().getId(),
            DocumentConstant.DocumentType.N1_BRIDE,
            configId
        );
        String generatedNumber = StringUtils.hasText(existingNumber) ? existingNumber : this.generateNextDocumentNumber(documentConfig);

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
                    DocumentConstant.DocumentType.WN,
                    DocumentConstant.DocumentType.UPDATE_HISTORY
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
                DocumentConstant.DocumentType.N5_GROOM,
                DocumentConstant.DocumentType.UPDATE_HISTORY
            )
        );

        return documentTypes;
    }

    private DocumentTemplate findTemplateByType(DocumentConstant.DocumentType documentType, UUID configId) {
        return this.documentTemplateRepository.findByDocumentTypeAndConfig_IdAndDeletedFalse(documentType, configId).orElse(null);
    }

    private GeneratedDocument save(GeneratedDocument generatedDocument) {
        return this.generatedDocumentRepository.save(generatedDocument);
    }

    @Transactional
    private byte[] generateZipBundleInternal(
        Marriage marriage,
        MarriageDocumentDto globalContext,
        List<DocumentConstant.DocumentType> documentTypesToProcess,
        Boolean isBride,
        String userWorkplaceId,
        Set<String> alreadyAddedFiles
    ) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
            this.generateAndAddDocumentsToZip(
                zos,
                marriage,
                globalContext,
                documentTypesToProcess,
                isBride,
                userWorkplaceId,
                alreadyAddedFiles
            );
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Transactional
    private void generateAndAddDocumentsToZip(
        ZipOutputStream zos,
        Marriage marriage,
        MarriageDocumentDto globalContext,
        List<DocumentConstant.DocumentType> documentTypesToProcess,
        Boolean isBride,
        String userWorkplaceId,
        Set<String> alreadyAddedFiles
    ) throws IOException {

        if (globalContext.getHistories().isEmpty()) {
            documentTypesToProcess.remove(DocumentConstant.DocumentType.UPDATE_HISTORY);
        }

        DocumentConfig config = this.findByWorkplaceIdAndServiceType(userWorkplaceId, DocumentConstant.ServiceType.MARRIAGE);
        if (config == null || config.isDeleted()) {
            throw new BusinessErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Konfigurasi dokumen tidak ditemukan untuk tempat kerja: " + userWorkplaceId);
        }

        var configId = config.getId();

        for (DocumentConstant.DocumentType docType : documentTypesToProcess) {
            log.info("Membuat tipe dokumen: {} untuk aplikasi {}", docType, marriage.getApplication().getId());

            var template = this.findTemplateByType(docType, configId);
            if (template == null) {
                throw new BusinessErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Template tidak ditemukan untuk tipe dokumen: " + docType);
            }

            Context thymeleafContext = new Context();
            thymeleafContext.setVariable("ctx", globalContext);

            byte[] pdfBytes = generatePdfFromTemplate(template.getFilePath(), thymeleafContext);

            String pdfFileName = generatePdfFilename(docType, marriage);
            if (alreadyAddedFiles.contains(pdfFileName)) {
                log.warn("File {} sudah ada di ZIP, skip duplikasi.", pdfFileName);
                continue;
            }
            addFileToZip(zos, pdfFileName, pdfBytes);
            alreadyAddedFiles.add(pdfFileName);
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
        if (marriage.getApplication() != null && marriage.getApplication().getId() != null) {
            applicationNumber = CommonUtil.simplifyUUID(marriage.getApplication().getId());
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
            GeneratedDocument generatedDoc = generatedDocumentRepository
                .findByApplicationIdAndDocumentTemplateIdAndDeletedFalse(application.getId(), template.getId())
                .stream()
                .findFirst()
                .orElse(new GeneratedDocument());

            String dataSnapshotJson = this.objectMapper.writeValueAsString(dataUsed);

            generatedDoc.setApplication(application);
            generatedDoc.setDocumentTemplate(template);
            generatedDoc.setDocumentNumber(documentNumber);
            generatedDoc.setFilePath(generatedFilePath);
            generatedDoc.setDataSnapshot(dataSnapshotJson);
            generatedDoc.setIssuedAt(issueDate.atStartOfDay());

            this.generatedDocumentRepository.save(generatedDoc);

            log.info("Saved/Updated metadata for generated document: {}", documentNumber);
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

    private String findExistingDocumentNumberByApplication(UUID applicationId, DocumentConstant.DocumentType docType, UUID configId) {
        DocumentTemplate template = this.findTemplateByType(docType, configId);
        if (template == null) return null;

        return this.generatedDocumentRepository.findByApplicationIdAndDocumentTemplateIdAndDeletedFalse(applicationId, template.getId())
            .map(GeneratedDocument::getDocumentNumber)
            .orElse(null);
    }

    private DocumentTemplate findTemplateByContext(
        DocumentConstant.DocumentType documentType,
        DocumentConstant.ServiceType serviceType,
        String workplaceCode
    ) {
        return this.documentTemplateRepository.findTemplateByContext(documentType, serviceType, workplaceCode).orElse(null);
    }
}
