package com.example.public_transportation_project.Repositories;

import com.example.public_transportation_project.Models.Bus;
import com.example.public_transportation_project.Models.BusStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    @Query(value = "SELECT * FROM bus WHERE id = ?", nativeQuery = true)
    Optional<Bus> findByIdIncludeDeleted(Long id);

    boolean existsByLicensePlateAndIsActiveTrue(String licensePlate);

    @Modifying
    @Query(value = "DELETE FROM bus WHERE id = ?", nativeQuery = true)
    void hardDeleteById(Long id);

    boolean existsByLicensePlate(String licensePlate);

    List<Bus> findByStatusAndIsActiveTrue(BusStatus status);

    @Query(value = "SELECT * FROM bus WHERE "
            + "(:status IS NULL OR status = :status) AND "
            + "(:archiveMode = 'ACTIVE_ONLY' AND is_active = true OR "
            + " :archiveMode = 'DELETED_ONLY' AND is_active = false OR "
            + " :archiveMode = 'ALL')", nativeQuery = true)
    List<Bus> findBusesAdvanced(String status, String archiveMode);

    @Query(value = "SELECT * FROM bus WHERE license_plate = :licensePlate", nativeQuery = true)
    Optional<Bus> findByLicensePlateIncludeDeleted(@Param("licensePlate") String licensePlate);
}
