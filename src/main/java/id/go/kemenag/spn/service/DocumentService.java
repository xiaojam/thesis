package id.go.kemenag.spn.service;

import id.go.kemenag.spn.config.custom.CustomUserDetails;
import id.go.kemenag.spn.constant.DocumentConstant;
import id.go.kemenag.spn.entity.divorce.DivorceCase;
import id.go.kemenag.spn.entity.document.DocumentConfig;
import id.go.kemenag.spn.entity.marriage.Marriage;

public interface DocumentService {

    byte[] downloadMarriageDocument(
        Marriage marriage,
        CustomUserDetails user,
        DocumentConstant.BundleMarriageType bundleMarriageType
    );

    DocumentConfig findByWorkplaceIdAndServiceType(String workplaceId, DocumentConstant.ServiceType serviceType);

    byte[] downloadDivorceDocument(DivorceCase divorceCase);
}
