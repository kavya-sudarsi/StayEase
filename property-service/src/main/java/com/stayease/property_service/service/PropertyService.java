package com.stayease.property_service.service;

import com.stayease.property_service.dto.PropertyRequest;
import com.stayease.property_service.entity.GenderType;
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

    // GET ALL PROPERTIES
    public List<Property> getAllProperties() {

        return propertyRepository.findAll();
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

    // GET OWNER PROPERTIES
    public List<Property> getOwnerProperties(
            String email
    ) {
        return propertyRepository.findByOwnerEmail(email);
    }

    // UPDATE PROPERTY
    public Property updateProperty(
            Long propertyId,
            PropertyRequest request,
            String email
    ) {
        Property property = getProperty(propertyId);

        if (!property.getOwnerEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can update only your property"
            );
        }

        property.setName(request.getName());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setPincode(request.getPincode());
        property.setDescription(request.getDescription());
        property.setPropertyType(request.getPropertyType());
        property.setGender(request.getGender());
        property.setContactNumber(request.getContactNumber());
        property.setImageUrl(request.getImageUrl());
        property.setAmenities(request.getAmenities());

        return propertyRepository.save(property);
    }

    // DELETE PROPERTY
    public void deleteProperty(Long id, String email) {

        Property property = getProperty(id);

        if (!property.getOwnerEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can delete only your property"
            );
        }

        propertyRepository.delete(property);
    }

    // SEARCH BY CITY
    public List<Property> searchByCity(String city) {

        return propertyRepository.findByCityIgnoreCase(city);
    }

    // FILTER BY GENDER
    public List<Property> filterByGender(GenderType gender) {

        return propertyRepository.findByGender(gender);
    }

    // FILTER BY PRICE
    public List<Property> filterByPrice(
            Double minPrice,
            Double maxPrice
    ) {
        return propertyRepository.findByPriceRange(
                minPrice,
                maxPrice
        );
    }
}