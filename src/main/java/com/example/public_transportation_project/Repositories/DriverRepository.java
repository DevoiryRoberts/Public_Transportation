package com.example.public_transportation_project.Repositories;

import com.example.public_transportation_project.Models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByIsActiveTrue();

    boolean existsByEmail(String email);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByPhone(String phone);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    @Query(value = "SELECT * FROM driver WHERE id = ?1", nativeQuery = true)
    Optional<Driver> findByIdIncludeDeleted(Long id);

    List<Driver> findByDeletionRequestedTrue();

    long countByRoleAndIsActiveTrue(String role);

    @Modifying
    @Query(value = "DELETE FROM driver WHERE id = ?1", nativeQuery = true)
    void hardDeleteById(Long id);

    @Query(value = "SELECT COUNT(*) > 0 FROM driver WHERE email = :email", nativeQuery = true)
    boolean existsByEmailIncludeDeleted(@Param("email") String email);

    @Query(value = "SELECT COUNT(*) > 0 FROM driver WHERE license_number = :licenseNumber", nativeQuery = true)
    boolean existsByLicenseNumberIncludeDeleted(@Param("licenseNumber") String licenseNumber);

    @Query(value = "SELECT * FROM driver WHERE email = :email", nativeQuery = true)
    Optional<Driver> findByEmailIncludeDeleted(@Param("email") String email);

    List<Driver> findByRatingAndIsActiveTrue(int rating);

    List<Driver> findByRatingGreaterThanEqualAndIsActiveTrue(int rating);

    Optional<Driver> findByEmail(String email);
}
