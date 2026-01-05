package id.go.kemenag.spn.repository.document;

import id.go.kemenag.spn.constant.DocumentConstant;
import id.go.kemenag.spn.entity.document.DocumentTemplate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentTemplateRepository extends CrudRepository<DocumentTemplate, UUID> {

    Optional<DocumentTemplate> findByDocumentTypeAndConfig_IdAndDeletedFalse(
        DocumentConstant.DocumentType documentType,
        UUID configId
    );

    @Query(
        "SELECT dt FROM DocumentTemplate dt " +
        "JOIN dt.config dc " +
        "JOIN dc.workplace w " +
        "WHERE dt.documentType = :documentType " +
        "AND dc.serviceType = :serviceType " +
        "AND w.code = :workplaceCode " +
        "AND dt.deleted = false AND dc.deleted = false"
    )
    Optional<DocumentTemplate> findTemplateByContext(
        @Param("documentType") DocumentConstant.DocumentType documentType,
        @Param("serviceType") DocumentConstant.ServiceType serviceType,
        @Param("workplaceCode") String workplaceCode
    );
}
