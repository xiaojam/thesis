package id.go.kemenag.spn.repository.master;

import id.go.kemenag.spn.entity.master.Master;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MasterRepository extends CrudRepository<Master, UUID> {

    Optional<Master> findByGroupNameAndCodeAndDeletedFalse(String groupName, String code);
}
