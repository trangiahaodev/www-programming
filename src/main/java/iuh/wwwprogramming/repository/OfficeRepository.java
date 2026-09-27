package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficeRepository extends JpaRepository<Office, String> {
    List<Office> findByActiveTrue();
}
