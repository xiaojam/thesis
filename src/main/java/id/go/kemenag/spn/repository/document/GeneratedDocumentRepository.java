package id.go.kemenag.spn.repository.document;

import id.go.kemenag.spn.entity.document.GeneratedDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GeneratedDocumentRepository extends CrudRepository<GeneratedDocument, UUID> {
}
