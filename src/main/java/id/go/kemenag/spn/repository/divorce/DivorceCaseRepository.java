package id.go.kemenag.spn.repository.divorce;

import id.go.kemenag.spn.entity.divorce.DivorceCase;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DivorceCaseRepository extends CrudRepository<DivorceCase, UUID> {

    Optional<DivorceCase> findFirstByApplicationIdAndDeletedIsFalseOrderByCreatedAtDesc(UUID applicationId);

    Iterable<DivorceCase> findAllByApplicationIdInAndDeletedIsFalseOrderByCreatedAtAsc(List<UUID> applicationIds);

    Optional<DivorceCase> findFirstByCaseNumberAndDeletedIsFalseOrderByCreatedAtDesc(String caseNumber);
}
