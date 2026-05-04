package com.example.public_transportation_project.Repositories;

import com.example.public_transportation_project.Models.StationInLine;
import com.example.public_transportation_project.Models.StationInLineId; // ייבוא המפתח החדש
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationInLineRepository extends JpaRepository<StationInLine, StationInLineId> {

    List<StationInLine> findByBusLineIdAndIsActiveTrueOrderByStationOrderAsc(Long busLineId);

    List<StationInLine> findByStationIdAndIsActiveTrue(Long stationId);

    boolean existsByBusLineIdAndStationIdAndIsActiveTrue(Long busLineId, Long stationId);

    Optional<StationInLine> findByBusLineIdAndStationId(Long busLineId, Long stationId);

    @Modifying
    @Transactional
    @Query("DELETE FROM StationInLine s WHERE s.busLine.id = :busLineId")
    void deleteByBusLineId(@Param("busLineId") Long busLineId);

    List<StationInLine> findByBusLineId(Long busLineId);
}
