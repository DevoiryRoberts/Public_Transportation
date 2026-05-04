package com.example.public_transportation_project.Repositories;

import com.example.public_transportation_project.Models.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    @Query(value = "SELECT * FROM station WHERE id = ?", nativeQuery = true)
    Optional<Station> findByIdIncludeDeleted(Long id);

    boolean existsByNameAndAddressAndIsActiveTrue(String name, String address);

    List<Station> findByNameContainingAndIsActiveTrue(String name);

    @Modifying
    @Query(value = "DELETE FROM station WHERE id = ?", nativeQuery = true)
    void hardDeleteById(Long id);

    @Query(value = "SELECT * FROM station s WHERE "
            + "((:mode = 'ACTIVE_ONLY' AND s.is_active = true) OR "
            + " (:mode = 'DELETED_ONLY' AND s.is_active = false) OR "
            + " (:mode = 'ALL'))", nativeQuery = true)
    List<Station> findStationsAdvanced(@Param("mode") String mode);

    @Query(value = "SELECT * FROM station WHERE name = :name AND address = :address", nativeQuery = true)
    Optional<Station> findByNameAndAddressIncludeDeleted(@Param("name") String name, @Param("address") String address);
}
