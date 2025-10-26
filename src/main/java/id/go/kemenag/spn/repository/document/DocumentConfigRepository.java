package id.go.kemenag.spn.repository.document;

import id.go.kemenag.spn.constant.DocumentConstant;
import id.go.kemenag.spn.entity.document.DocumentConfig;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentConfigRepository extends CrudRepository<DocumentConfig, UUID> {

    Optional<DocumentConfig> findByWorkplace_CodeAndServiceTypeAndDeletedFalse(
        String workplaceId,
        DocumentConstant.ServiceType serviceType
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT dc FROM DocumentConfig dc WHERE dc.id = :id")
    Optional<DocumentConfig> findByIdWithLock(UUID id);
}
