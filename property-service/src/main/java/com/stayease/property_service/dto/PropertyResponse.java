package com.stayease.property_service.dto;

import com.stayease.property_service.entity.GenderType;
import com.stayease.property_service.entity.PropertyStatus;
import com.stayease.property_service.entity.PropertyType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PropertyResponse {

    private Long id;

    private String name;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private String description;

    private PropertyType propertyType;

    private GenderType gender;

    private String contactNumber;

    private String imageUrl;

    private List<String> amenities;

    private PropertyStatus status;

    private LocalDateTime createdAt;

    private String ownerEmail;

    private List<RoomResponse> rooms;
}