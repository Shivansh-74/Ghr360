package com.ghr360.repository;

import com.ghr360.entity.MediaType;
import com.ghr360.entity.PropertyMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyMediaRepository extends JpaRepository<PropertyMedia, Long> {

    List<PropertyMedia> findByPropertyId(Long propertyId);

    List<PropertyMedia> findByPropertyIdAndMediaType(Long propertyId, MediaType mediaType);

    List<PropertyMedia> findByUserCode(String userCode);

    void deleteByPropertyId(Long propertyId);
}
