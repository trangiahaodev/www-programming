package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfficeRepository extends JpaRepository<Office, String> {

    @Query("SELECT o FROM Office o WHERE o.active = true AND (o.isDeleted = false OR o.isDeleted IS NULL)")
    List<Office> findByActiveTrue();

    List<Office> findByActiveTrueAndIsDeletedFalse();

    Optional<Office> findByIdAndIsDeletedFalse(String id);
}
