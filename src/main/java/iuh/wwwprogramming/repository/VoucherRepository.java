package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    @Query("SELECT v FROM Voucher v WHERE v.active = true AND (v.isDeleted = false OR v.isDeleted IS NULL)")
    List<Voucher> findByActiveTrue();

    List<Voucher> findByActiveTrueAndIsDeletedFalse();

    @Query("SELECT v FROM Voucher v WHERE LOWER(v.code) = LOWER(:code) AND v.active = true AND (v.isDeleted = false OR v.isDeleted IS NULL)")
    Optional<Voucher> findByCodeIgnoreCaseAndActiveTrue(@Param("code") String code);

    Optional<Voucher> findByCodeIgnoreCaseAndActiveTrueAndIsDeletedFalse(String code);

    Optional<Voucher> findByIdAndIsDeletedFalse(Long id);

    boolean existsByCode(String code);

    boolean existsByCodeAndIsDeletedFalse(String code);
}
