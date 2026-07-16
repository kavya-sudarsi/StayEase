package com.stayease.booking_service.dto;

import lombok.Data;


@Data
public class PropertyDetailsResponse {

    private Long id;

    private String name;

    private String city;

    private String state;

    private String imageUrl;


}