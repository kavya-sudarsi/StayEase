package com.stayease.property_service.dto;

import com.stayease.property_service.entity.GenderType;
import com.stayease.property_service.entity.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class PropertyRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;

    private String state;

    @Pattern(
            regexp = "^\\d{6}$",
            message = "Invalid pincode"
    )
    private String pincode;

    private String description;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    @NotNull(message = "Gender type is required")
    private GenderType gender;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid contact number"
    )
    private String contactNumber;

    private String imageUrl;

    private List<String> amenities;
}