package id.go.kemenag.spn.repository;

import id.go.kemenag.spn.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByUsernameAndPasswordAndDeletedFalse(String username, String password);

    Optional<User> findByUsernameAndDeletedFalse(String username);
}
