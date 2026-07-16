package com.stayease.property_service.controller;

import com.stayease.property_service.dto.*;
import com.stayease.property_service.entity.GenderType;
import com.stayease.property_service.entity.Property;
import com.stayease.property_service.entity.PropertyStatus;
import com.stayease.property_service.entity.Room;
import com.stayease.property_service.service.PropertyService;
import com.stayease.property_service.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.stayease.property_service.service.ImageUploadService;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final RoomService roomService;
    private final ImageUploadService imageUploadService;


    @PostMapping("/upload-image")
    public ResponseEntity<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(imageUploadService.uploadImage(file));
    }

    // CREATE PROPERTY
    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody PropertyRequest request) {

        validateOwner(role);

        Property property = Property.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .description(request.getDescription())
                .propertyType(request.getPropertyType())
                .gender(request.getGender())
                .contactNumber(request.getContactNumber())
                .imageUrl(request.getImageUrl())
                .amenities(request.getAmenities())
                .status(PropertyStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .ownerEmail(email)
                .build();

        Property saved = propertyService.createProperty(property);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToPropertyResponse(saved));
    }

    // ADD ROOM
    @PostMapping("/{propertyId}/rooms")
    public ResponseEntity<RoomResponse> addRoom(
            @PathVariable Long propertyId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody RoomRequest request) {

        validateOwner(role);

        Room room = roomService.addRoom(propertyId, request, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToRoomResponse(room));
    }

    // GET ALL PROPERTIES
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {

        List<PropertyResponse> properties =
                propertyService.getAllProperties()
                        .stream()
                        .map(this::mapToPropertyResponse)
                        .toList();

        return ResponseEntity.ok(properties);
    }

    // GET PROPERTY BY ID
    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> getPropertyById(
            @PathVariable Long propertyId) {

        Property property = propertyService.getProperty(propertyId);

        return ResponseEntity.ok(mapToPropertyResponse(property));
    }

    // SEARCH BY CITY
    @GetMapping("/search")
    public ResponseEntity<List<PropertyResponse>> searchByCity(
            @RequestParam String city) {

        List<PropertyResponse> properties =
                propertyService.searchByCity(city)
                        .stream()
                        .map(this::mapToPropertyResponse)
                        .toList();

        return ResponseEntity.ok(properties);
    }

    // FILTER BY GENDER
    @GetMapping("/filter/gender")
    public ResponseEntity<List<PropertyResponse>> filterByGender(
            @RequestParam String gender) {

        GenderType genderType;
        try {
            genderType = GenderType.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid gender type. Use MALE, FEMALE, or CO_ED"
            );
        }

        List<PropertyResponse> properties =
                propertyService.filterByGender(genderType)
                        .stream()
                        .map(this::mapToPropertyResponse)
                        .toList();

        return ResponseEntity.ok(properties);
    }

    // FILTER BY PRICE
    @GetMapping("/filter/price")
    public ResponseEntity<List<PropertyResponse>> filterByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {

        List<PropertyResponse> properties =
                propertyService.filterByPrice(minPrice, maxPrice)
                        .stream()
                        .map(this::mapToPropertyResponse)
                        .toList();

        return ResponseEntity.ok(properties);
    }

    // GET OWNER PROPERTIES
    @GetMapping("/my-properties")
    public ResponseEntity<List<PropertyResponse>> getOwnerProperties(
            @RequestHeader("X-User-Email") String email) {

        List<PropertyResponse> properties =
                propertyService.getOwnerProperties(email)
                        .stream()
                        .map(this::mapToPropertyResponse)
                        .toList();

        return ResponseEntity.ok(properties);
    }

    // UPDATE PROPERTY
    @PutMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable Long propertyId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody PropertyRequest request) {

        validateOwner(role);

        Property updated = propertyService.updateProperty(
                propertyId, request, email);

        return ResponseEntity.ok(mapToPropertyResponse(updated));
    }

    // DELETE PROPERTY
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        validateOwner(role);

        propertyService.deleteProperty(id, email);

        return ResponseEntity.noContent().build();
    }

    // GET ROOM
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomResponse> getRoom(
            @PathVariable Long roomId) {

        Room room = roomService.getRoom(roomId);

        return ResponseEntity.ok(mapToRoomResponse(room));
    }

    // UPDATE ROOM
    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long roomId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody RoomRequest request) {

        validateOwner(role);

        Room updated = roomService.updateRoom(roomId, request, email);

        return ResponseEntity.ok(mapToRoomResponse(updated));
    }

    // DELETE ROOM
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long roomId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        validateOwner(role);

        roomService.deleteRoom(roomId, email);

        return ResponseEntity.noContent().build();
    }

    // DECREASE BEDS
    @PutMapping("/rooms/{roomId}/decrease")
    public ResponseEntity<Void> decreaseAvailableBeds(
            @PathVariable Long roomId) {

        roomService.decreaseAvailableBeds(roomId);

        return ResponseEntity.ok().build();
    }

    // INCREASE BEDS
    @PutMapping("/rooms/{roomId}/increase")
    public ResponseEntity<Void> increaseAvailableBeds(
            @PathVariable Long roomId) {

        roomService.increaseAvailableBeds(roomId);

        return ResponseEntity.ok().build();
    }

    // ROLE VALIDATION HELPER
    private void validateOwner(String role) {
        if (!role.equals("OWNER")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only OWNER can perform this action"
            );
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PropertyResponse>> filterProperties(

            @RequestParam(required = false)
            String city,

            @RequestParam(required = false)
            String gender,

            @RequestParam(required = false)
            Double minPrice,

            @RequestParam(required = false)
            Double maxPrice
    ) {

        GenderType genderType = null;

        if (gender != null && !gender.isBlank()) {

            try {

                genderType =
                        GenderType.valueOf(
                                gender.toUpperCase()
                        );

            } catch (IllegalArgumentException e) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid gender type"
                );
            }
        }

        List<PropertyResponse> properties =
                propertyService
                        .filterProperties(
                                city,
                                genderType,
                                minPrice,
                                maxPrice
                        )
                        .stream()
                        .map(this::mapToPropertyResponse)
                        .toList();

        return ResponseEntity.ok(properties);
    }
    // PROPERTY RESPONSE MAPPING
    private PropertyResponse mapToPropertyResponse(
            Property property) {

        List<RoomResponse> rooms = null;

        if (property.getRooms() != null) {
            rooms = property.getRooms()
                    .stream()
                    .map(this::mapToRoomResponse)
                    .toList();
        }

        return PropertyResponse.builder()
                .id(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .city(property.getCity())
                .state(property.getState())
                .pincode(property.getPincode())
                .description(property.getDescription())
                .propertyType(property.getPropertyType())
                .gender(property.getGender())
                .contactNumber(property.getContactNumber())
                .imageUrl(property.getImageUrl())
                .amenities(property.getAmenities())
                .status(property.getStatus())
                .createdAt(property.getCreatedAt())
                .ownerEmail(property.getOwnerEmail())
                .rooms(rooms)
                .build();
    }

    // ROOM RESPONSE MAPPING
    private RoomResponse mapToRoomResponse(Room room) {

        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .totalBeds(room.getTotalBeds())
                .availableBeds(room.getAvailableBeds())
                .pricePerBed(room.getPricePerBed())
                .wifi(room.isWifi())
                .ac(room.isAc())
                .build();
    }
}