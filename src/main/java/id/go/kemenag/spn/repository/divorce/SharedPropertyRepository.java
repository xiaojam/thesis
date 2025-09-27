package id.go.kemenag.spn.repository.divorce;

import id.go.kemenag.spn.entity.divorce.SharedProperty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SharedPropertyRepository extends CrudRepository<SharedProperty, UUID> {
}
