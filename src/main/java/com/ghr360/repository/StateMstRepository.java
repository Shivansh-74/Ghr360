package com.ghr360.repository;

import com.ghr360.entity.StateMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateMstRepository extends JpaRepository<StateMst, Long> {
    List<StateMst> findAllByOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}
