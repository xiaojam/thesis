package id.go.kemenag.spn.repository.document;

import id.go.kemenag.spn.constant.DocumentConstant;
import id.go.kemenag.spn.entity.document.DocumentTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentTemplateRepository extends CrudRepository<DocumentTemplate, UUID> {

    Optional<DocumentTemplate> findByDocumentTypeAndDeletedFalse(DocumentConstant.DocumentType documentType);
}
