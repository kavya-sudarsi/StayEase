package com.stayease.property_service.repository;

import com.stayease.property_service.entity.GenderType;
import com.stayease.property_service.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwnerEmail(String ownerEmail);

    List<Property> findByCityIgnoreCase(String city);

    List<Property> findByGender(GenderType gender);

    @Query("SELECT DISTINCT p FROM Property p " +
            "JOIN p.rooms r " +
            "WHERE r.pricePerBed BETWEEN :minPrice AND :maxPrice")
    List<Property> findByPriceRange(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    @Query("""
    SELECT DISTINCT p
    FROM Property p
    JOIN p.rooms r
    WHERE
    (:city IS NULL OR LOWER(p.city) = LOWER(:city))
    AND
    (:gender IS NULL OR p.gender = :gender)
    AND
    (:minPrice IS NULL OR r.pricePerBed >= :minPrice)
    AND
    (:maxPrice IS NULL OR r.pricePerBed <= :maxPrice)
    """)
    List<Property> filterProperties(
            @Param("city") String city,
            @Param("gender") GenderType gender,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );
}