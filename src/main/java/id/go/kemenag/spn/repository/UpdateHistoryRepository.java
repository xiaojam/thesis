package id.go.kemenag.spn.repository;

import id.go.kemenag.spn.entity.UpdateHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UpdateHistoryRepository extends CrudRepository<UpdateHistory, UUID> {

    Iterable<UpdateHistory> findByApplicationIdAndDeletedIsFalse(UUID applicationId);
}
