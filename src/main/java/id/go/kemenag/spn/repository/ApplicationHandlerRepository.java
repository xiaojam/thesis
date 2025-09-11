package id.go.kemenag.spn.repository;

import id.go.kemenag.spn.entity.ApplicationHandler;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationHandlerRepository extends CrudRepository<ApplicationHandler, UUID> {

    Iterable<ApplicationHandler> findAllByApplicationIdAndDeletedFalse(UUID applicationId);
}
