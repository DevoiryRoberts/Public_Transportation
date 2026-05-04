package com.example.public_transportation_project.Repositories;

import com.example.public_transportation_project.Models.BusLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface BusLineRepository extends JpaRepository<BusLine, Long> {

    List<BusLine> findByIsActiveTrue();

    @Query(value = "SELECT * FROM bus_line WHERE is_active = false", nativeQuery = true)
    List<BusLine> findByIsActiveFalse();

    Optional<BusLine> findByLineNumberAndIsActiveTrue(int lineNumber);

    @Query("SELECT COUNT(b) > 0 FROM BusLine b WHERE b.lineNumber = :num AND b.origin = :origin AND b.destination = :dest")
    boolean existsByDetails(@Param("num") int num, @Param("origin") String origin, @Param("dest") String dest);

    @Query(value = "SELECT * FROM bus_line WHERE id = :id", nativeQuery = true)
    Optional<BusLine> findByIdIncludeDeleted(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM bus_line WHERE id = :id", nativeQuery = true)
    void hardDeleteById(@Param("id") Long id);

    @Query(value = "SELECT * FROM bus_line", nativeQuery = true)
    List<BusLine> findAllIncludeDeleted();
}
