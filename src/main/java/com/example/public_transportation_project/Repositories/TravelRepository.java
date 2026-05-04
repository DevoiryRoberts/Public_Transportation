package com.example.public_transportation_project.Repositories;

import com.example.public_transportation_project.Models.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {

    List<Travel> findByIsActiveTrue();

    List<Travel> findByBusLineIdAndIsActiveTrue(Long busLineId);

    Optional<Travel> findTopByBusLineIdAndIsActiveTrueOrderByDepartureTimeDesc(Long busLineId);

    @Query(value = "SELECT COUNT(*) > 0 FROM travel WHERE driver_id = :driverId "
            + "AND is_active = true "
            + "AND (:startTime < arrival_time AND :endTime > departure_time)", nativeQuery = true)
    boolean isDriverBusy(@Param("driverId") Long driverId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query(value = "SELECT COUNT(*) > 0 FROM travel WHERE bus_id = :busId "
            + "AND is_active = true "
            + "AND (:startTime < arrival_time AND :endTime > departure_time)", nativeQuery = true)
    boolean isBusBusy(@Param("busId") Long busId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    boolean existsByBusLineIdAndDepartureTimeAfter(Long busLineId, LocalDateTime dateTime);

    @Modifying
    @Transactional
    @Query("DELETE FROM Travel t WHERE t.busLine.id = :lineId")
    void deleteByBusLineId(@Param("lineId") Long lineId);

    @Query(value = "SELECT * FROM travel", nativeQuery = true)
    List<Travel> findAllIncludingArchive();

    @Query(value = "SELECT * FROM travel WHERE "
            + "(:archiveMode = 'ALL' OR "
            + " (:archiveMode = 'ACTIVE_ONLY' AND is_active = true) OR "
            + " (:archiveMode = 'DELETED_ONLY' AND is_active = false)) "
            + "AND (:status IS NULL OR status = :status)", nativeQuery = true)
    List<Travel> findAdminTravels(@Param("archiveMode") String archiveMode,
            @Param("status") String status);

    @Query(value = "SELECT * FROM travel WHERE is_active = false", nativeQuery = true)
    List<Travel> findDeletedOnly();
}
