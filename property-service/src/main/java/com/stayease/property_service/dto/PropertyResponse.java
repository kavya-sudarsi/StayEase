package com.stayease.property_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PropertyResponse {

    private Long id;
    private String name;
    private String address;
    private String ownerEmail;
    private List<RoomResponse> rooms;
}