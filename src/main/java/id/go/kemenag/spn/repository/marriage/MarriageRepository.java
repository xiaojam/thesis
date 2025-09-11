package id.go.kemenag.spn.repository.marriage;

import id.go.kemenag.spn.entity.marriage.Marriage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarriageRepository extends CrudRepository<Marriage, UUID> {

    Iterable<Marriage> findALlByApplicationIdInAndDeletedFalseOrderByCreatedAtAsc(List<UUID> applicationIds);

    Optional<Marriage> findByApplicationIdAndDeletedFalse(UUID applicationId);
}
