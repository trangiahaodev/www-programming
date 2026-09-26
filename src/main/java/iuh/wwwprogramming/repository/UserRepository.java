package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUserCode(String userCode);

    boolean existsByEmail(String email);

    Optional<User> findByUserCode(String userCode);
}
