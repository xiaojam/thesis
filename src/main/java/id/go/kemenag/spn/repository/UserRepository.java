package id.go.kemenag.spn.repository;

import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.entity.UserDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserDetail, UUID> {

    Optional<UserDetail> findFirstByUsernameAndPasswordAndDeletedFalse(String username, String password);

    Optional<UserDetail> findFirstByUsernameAndDeletedFalse(String username);

    Optional<UserDetail> findFirstByRoleAndWorkplaceCodeAndDeletedFalse(AuthConstant.Role role, String workplaceCode);
}
