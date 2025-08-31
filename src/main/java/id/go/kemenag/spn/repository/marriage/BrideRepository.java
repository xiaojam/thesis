package id.go.kemenag.spn.repository.marriage;

import id.go.kemenag.spn.entity.marriage.Bride;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrideRepository extends CrudRepository<Bride, UUID> {

    Optional<Bride> findFirstByIdentityIdAndDeletedIsFalseOrderByCreatedAtDesc(String identityId);

    Iterable<Bride> findAllByApplicationIdInAndDeletedIsFalseOrderByCreatedAtAsc(Iterable<UUID> applicationIds);
}
