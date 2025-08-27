package id.go.kemenag.spn.repository.marriage;

import id.go.kemenag.spn.entity.marriage.Marriage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MarriageRepository extends CrudRepository<Marriage, UUID> {
}
