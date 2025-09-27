package id.go.kemenag.spn.repository.divorce;

import id.go.kemenag.spn.entity.divorce.DivorceCase;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DivorceCaseRepository extends CrudRepository<DivorceCase, UUID> {
}
