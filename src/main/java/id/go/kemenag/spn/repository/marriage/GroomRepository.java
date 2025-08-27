package id.go.kemenag.spn.repository.marriage;

import id.go.kemenag.spn.entity.marriage.Groom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroomRepository extends CrudRepository<Groom, UUID> {

    Optional<Groom> findFirstByIdentityIdAndDeletedIsFalseOrderByCreatedAtDesc(String identityId);
}
