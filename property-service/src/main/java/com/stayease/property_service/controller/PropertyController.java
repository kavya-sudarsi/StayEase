package com.stayease.property_service.controller;

import com.stayease.property_service.dto.*;
import com.stayease.property_service.entity.Property;
import com.stayease.property_service.entity.Room;
import com.stayease.property_service.service.PropertyService;
import com.stayease.property_service.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final RoomService roomService;

    // Create Property
    @PostMapping
    public PropertyResponse createProperty(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody PropertyRequest request) {

        if (!role.equals("OWNER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only OWNER can create property");
        }

        Property property = Property.builder()
                .name(request.getName())
                .address(request.getAddress())
                .ownerEmail(email)
                .build();

        Property saved = propertyService.createProperty(property);

        return mapToPropertyResponse(saved);
    }

    // Add Room
    @PostMapping("/{propertyId}/rooms")
    public RoomResponse addRoom(
            @PathVariable Long propertyId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody RoomRequest request) {

        if (!role.equals("OWNER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only OWNER can add rooms");
        }

        Room room = roomService.addRoom(propertyId, request, email);

        return mapToRoomResponse(room);
    }

    // Get All Properties
    @GetMapping
    public List<PropertyResponse> getAllProperties() {

        return propertyService.getAllProperties()
                .stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    // Get Room By ID
    @GetMapping("/rooms/{roomId}")
    public RoomResponse getRoom(@PathVariable Long roomId) {

        Room room = roomService.getRoom(roomId);

        return mapToRoomResponse(room);
    }

    // Decrease Beds
    @PutMapping("/rooms/{roomId}/decrease")
    public ResponseEntity<Void> decreaseAvailableBeds(
            @PathVariable Long roomId) {

        roomService.decreaseAvailableBeds(roomId);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/rooms/{roomId}/increase")
    public ResponseEntity<Void> increaseAvailableBeds(
            @PathVariable Long roomId) {

        roomService.increaseAvailableBeds(roomId);
        return ResponseEntity.ok().build();
    }
    // ===== Mapping Methods =====

    private PropertyResponse mapToPropertyResponse(Property property) {

        List<RoomResponse> rooms = null;

        if (property.getRooms() != null) {
            rooms = property.getRooms()
                    .stream()
                    .map(this::mapToRoomResponse)
                    .collect(Collectors.toList());
        }

        return PropertyResponse.builder()
                .id(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .ownerEmail(property.getOwnerEmail())
                .rooms(rooms)
                .build();
    }

    private RoomResponse mapToRoomResponse(Room room) {

        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .totalBeds(room.getTotalBeds())
                .availableBeds(room.getAvailableBeds())
                .build();
    }
}