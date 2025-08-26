package id.go.kemenag.spn.repository.marriage;

import id.go.kemenag.spn.entity.marriage.Guardian;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GuardianRepository extends CrudRepository<Guardian, UUID> {
}
