package id.go.kemenag.spn.repository;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.entity.Application;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, UUID> {

    Iterable<Application> findAllByDeletedFalseAndStatusInAndTypeOrderByCreatedAtAsc(
        List<ApplicationConstant.Status> statusList,
        ApplicationConstant.Type type
    );
}
