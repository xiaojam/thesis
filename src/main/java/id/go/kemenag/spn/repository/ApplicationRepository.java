package id.go.kemenag.spn.repository;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.entity.Application;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, UUID> {

    Iterable<Application> findAllByDeletedFalseAndStatusInAndTypeOrderByCreatedAtAsc(
        List<ApplicationConstant.Status> statusList,
        ApplicationConstant.Type type
    );

    Iterable<Application> findAllByStatusAndCreatedAtBeforeAndDeletedFalse(
        ApplicationConstant.Status status,
        ZonedDateTime createdAt
    );

    Optional<Application> findByIdAndDeletedFalse(UUID id);

    @Query(
        value = """
        select a.*
        from application a
        join application_handler ah
          on a.id = ah.application_id
        join (
            select application_id, max(created_at) as last_created_at
            from application_handler
            group by application_id
        ) ah_last
          on ah.application_id = ah_last.application_id
         and ah.created_at = ah_last.last_created_at
        where a.status in (:#{#statusList.![name()]})
          and a.type = :#{#type.name()}
          and ah.role = :#{#handlerRole.name()}
          and ah.workplace_code = :handlerWorkplaceCode
          and a.deleted is false
        """,
        nativeQuery = true
    )
    Iterable<Application> findAllByHandler(
        List<ApplicationConstant.Status> statusList,
        ApplicationConstant.Type type,
        AuthConstant.Role handlerRole,
        String handlerWorkplaceCode
    );

    @Query(
        value = """
        select a.*
        from application a
        join application_handler ah
          on a.id = ah.application_id
        join (
            select application_id, max(created_at) as last_created_at
            from application_handler
            group by application_id
        ) ah_last
          on ah.application_id = ah_last.application_id
         and ah.created_at = ah_last.last_created_at
        where a.id = :applicationId
          and ah.role = :#{#handlerRole.name()}
          and ah.workplace_code = :handlerWorkplaceCode
          and a.deleted is false
        """,
        nativeQuery = true
    )
    Optional<Application> findOneByHandler(
        UUID applicationId,
        AuthConstant.Role handlerRole,
        String handlerWorkplaceCode
    );
}
