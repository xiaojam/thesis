package id.go.kemenag.spn.repository.divorce;

import id.go.kemenag.spn.entity.divorce.Plaintiff;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaintiffRepository extends CrudRepository<Plaintiff, UUID> {
}
