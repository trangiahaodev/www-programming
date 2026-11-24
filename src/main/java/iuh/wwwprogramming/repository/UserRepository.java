package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.email = :email AND (u.isDeleted = false OR u.isDeleted IS NULL)")
    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIsDeletedFalse(String email);

    @Query("SELECT u FROM User u WHERE u.userCode = :userCode AND (u.isDeleted = false OR u.isDeleted IS NULL)")
    Optional<User> findByUserCode(@Param("userCode") String userCode);

    Optional<User> findByUserCodeAndIsDeletedFalse(String userCode);

    Optional<User> findByIdAndIsDeletedFalse(String id);

    @Query(
        value = """
            SELECT u FROM User u
            WHERE (u.isDeleted = false OR u.isDeleted IS NULL)
              AND (:code IS NULL OR :code = ''
                   OR LOWER(u.userCode) LIKE LOWER(CONCAT('%', :code, '%'))
                   OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
        """,
        countQuery = """
            SELECT COUNT(u) FROM User u
            WHERE (u.isDeleted = false OR u.isDeleted IS NULL)
              AND (:code IS NULL OR :code = ''
                   OR LOWER(u.userCode) LIKE LOWER(CONCAT('%', :code, '%'))
                   OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
        """
    )
    Page<User> findByUserCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        @Param("code") String code, @Param("name") String name, @Param("email") String email, Pageable pageable);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, String id);

    boolean existsByEmailIgnoreCaseAndIdNotAndIsDeletedFalse(String email, String id);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.role = 'ROLE_ADMIN' and u.active = true and (u.isDeleted = false or u.isDeleted is null) order by u.id")
    List<User> lockActiveAdmins();

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id and (u.isDeleted = false or u.isDeleted is null)")
    Optional<User> findForUpdate(@Param("id") String id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIsDeletedFalse(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) AND (u.isDeleted = false OR u.isDeleted IS NULL)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);

    Optional<User> findByEmailIgnoreCaseAndIsDeletedFalse(String email);

    boolean existsByUserCode(String code);

    boolean existsByUserCodeAndIsDeletedFalse(String code);
}
