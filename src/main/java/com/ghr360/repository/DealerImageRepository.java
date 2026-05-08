package com.ghr360.repository;

import com.ghr360.entity.DealerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealerImageRepository extends JpaRepository<DealerImage, Long> {
    List<DealerImage> findByPropertyId(Long propertyId);
}
