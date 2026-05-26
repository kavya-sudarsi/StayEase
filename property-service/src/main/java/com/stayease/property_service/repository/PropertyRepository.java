package com.stayease.property_service.repository;

import com.stayease.property_service.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwnerEmail(String ownerEmail);
}