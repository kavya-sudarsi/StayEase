package com.stayease.property_service.service;

import com.stayease.property_service.entity.Property;
import com.stayease.property_service.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    // CREATE PROPERTY
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    // GET ALL PROPERTIES (PUBLIC)
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // GET OWNER PROPERTIES
    public List<Property> getOwnerProperties(String email) {
        return propertyRepository.findByOwnerEmail(email);
    }

    // GET PROPERTY BY ID
    public Property getProperty(Long propertyId) {

        return propertyRepository.findById(propertyId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Property not found"
                        ));
    }

    // DELETE PROPERTY
    public void deleteProperty(Long id, String email) {

        Property property = propertyRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Property not found"
                        ));

        if (!property.getOwnerEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can delete only your property"
            );
        }

        propertyRepository.delete(property);
    }
}