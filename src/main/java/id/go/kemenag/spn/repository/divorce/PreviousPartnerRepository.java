package id.go.kemenag.spn.repository.divorce;

import id.go.kemenag.spn.entity.marriage.PreviousPartner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PreviousPartnerRepository extends CrudRepository<PreviousPartner, UUID> {
}
