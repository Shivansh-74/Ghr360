package com.ghr360.repository;

import com.ghr360.entity.CityMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityMstRepository extends JpaRepository<CityMst, Long> {
    List<CityMst> findAllByOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}
