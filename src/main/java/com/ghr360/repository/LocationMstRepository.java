package com.ghr360.repository;

import com.ghr360.entity.LocationMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationMstRepository extends JpaRepository<LocationMst, Long> {

    Optional<LocationMst> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT l FROM LocationMst l WHERE l.type = 'STATE' AND l.isActive = true ORDER BY l.name ASC")
    List<LocationMst> findActiveStates();

    @Query("SELECT l FROM LocationMst l WHERE l.type = 'CITY' AND l.parentId = :stateId AND l.isActive = true ORDER BY l.name ASC")
    List<LocationMst> findActiveCitiesByState(@Param("stateId") Long stateId);

    @Query("SELECT l FROM LocationMst l WHERE l.type = 'CITY' AND l.parentCode = :stateCode AND l.isActive = true ORDER BY l.name ASC")
    List<LocationMst> findActiveCitiesByStateCode(@Param("stateCode") String stateCode);
}
