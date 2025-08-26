package id.go.kemenag.spn.repository.divorce;

import id.go.kemenag.spn.entity.marriage.GroomMother;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroomMotherRepository extends CrudRepository<GroomMother, UUID> {
}
