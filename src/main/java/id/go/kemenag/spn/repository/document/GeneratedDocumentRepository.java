package id.go.kemenag.spn.repository.document;

import id.go.kemenag.spn.entity.document.GeneratedDocument;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneratedDocumentRepository extends CrudRepository<GeneratedDocument, UUID> {

    Optional<GeneratedDocument> findByApplicationIdAndDocumentTemplateIdAndDeletedFalse(UUID applicationId, UUID id);
}
