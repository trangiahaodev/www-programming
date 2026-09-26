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

    org.springframework.data.domain.Page<User> findByUserCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String code, String name, String email, org.springframework.data.domain.Pageable pageable);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, String id);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from User u where u.role = 'ROLE_ADMIN' and u.active = true order by u.id")
    java.util.List<User> lockActiveAdmins();

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from User u where u.id = :id")
    Optional<User> findForUpdate(@org.springframework.data.repository.query.Param("id") String id);
}
