package com.ghr360.repository;

import com.ghr360.entity.DealerProperty;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealerPropertyRepository extends JpaRepository<DealerProperty, Long>,
        JpaSpecificationExecutor<DealerProperty> {

    List<DealerProperty> findByDealerUsername(String dealerUsername);

    List<DealerProperty> findByType(String type);

    List<DealerProperty> findByCity(String city);

    // ✅ Total properties
    @Query("SELECT COUNT(p) FROM DealerProperty p")
    long countAllProperties();

    // ✅ Rent count
    @Query("SELECT COUNT(p) FROM DealerProperty p WHERE p.type = 'RENT'")
    long countRent();

    // ✅ Sale count
    @Query("SELECT COUNT(p) FROM DealerProperty p WHERE p.type = 'SALE'")
    long countSale();

    // ✅ Unique dealers (FIXED)
    @Query("SELECT COUNT(DISTINCT p.dealer.username) FROM DealerProperty p")
    long countDealers();

    // ✅ Recent properties
    @Query("SELECT p FROM DealerProperty p ORDER BY p.id DESC")
    List<DealerProperty> findRecent(Pageable pageable);
}