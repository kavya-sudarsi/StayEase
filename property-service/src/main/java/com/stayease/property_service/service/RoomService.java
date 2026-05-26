package com.stayease.property_service.service;

import com.stayease.property_service.dto.RoomRequest;
import com.stayease.property_service.entity.Property;
import com.stayease.property_service.entity.PropertyStatus;
import com.stayease.property_service.entity.Room;
import com.stayease.property_service.exception.NoBedsAvailableException;
import com.stayease.property_service.repository.PropertyRepository;
import com.stayease.property_service.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    private final PropertyRepository propertyRepository;

    // ADD ROOM
    public Room addRoom(
            Long propertyId,
            RoomRequest request,
            String email
    ) {
        Property property = propertyRepository
                .findById(propertyId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Property not found"
                        ));

        if (!property.getOwnerEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not owner of this property"
            );
        }

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .totalBeds(request.getTotalBeds())
                .availableBeds(request.getTotalBeds())
                .pricePerBed(request.getPricePerBed())
                .wifi(request.isWifi())
                .ac(request.isAc())
                .property(property)
                .build();

        return roomRepository.save(room);
    }

    // GET ROOM
    public Room getRoom(Long roomId) {

        return roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room not found"
                        ));
    }

    // UPDATE ROOM
    @Transactional
    public Room updateRoom(
            Long roomId,
            RoomRequest request,
            String email
    ) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room not found"
                        ));

        if (!room.getProperty().getOwnerEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can update only your room"
            );
        }

        // FIX: recalculate availableBeds based on occupied beds
        int bedsOccupied =
                room.getTotalBeds() - room.getAvailableBeds();

        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setTotalBeds(request.getTotalBeds());
        room.setAvailableBeds(
                Math.max(0, request.getTotalBeds() - bedsOccupied)
        );
        room.setPricePerBed(request.getPricePerBed());
        room.setWifi(request.isWifi());
        room.setAc(request.isAc());

        return roomRepository.save(room);
    }

    // DELETE ROOM
    @Transactional
    public void deleteRoom(Long roomId, String email) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room not found"
                        ));

        if (!room.getProperty().getOwnerEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can delete only your room"
            );
        }

        roomRepository.delete(room);
    }

    // DECREASE BEDS
    @Transactional
    public void decreaseAvailableBeds(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room not found"
                        ));

        if (room.getAvailableBeds() <= 0) {
            throw new NoBedsAvailableException(
                    "No beds available for this room"
            );
        }

        room.setAvailableBeds(room.getAvailableBeds() - 1);

        updatePropertyStatus(room.getProperty());
    }

    // INCREASE BEDS
    @Transactional
    public void increaseAvailableBeds(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room not found"
                        ));

        if (room.getAvailableBeds() >= room.getTotalBeds()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Beds already at maximum capacity"
            );
        }

        room.setAvailableBeds(room.getAvailableBeds() + 1);

        updatePropertyStatus(room.getProperty());
    }

    // UPDATE PROPERTY STATUS HELPER
    private void updatePropertyStatus(Property property) {

        boolean fullyOccupied = property.getRooms()
                .stream()
                .allMatch(room -> room.getAvailableBeds() == 0);

        property.setStatus(
                fullyOccupied
                        ? PropertyStatus.FULLY_OCCUPIED
                        : PropertyStatus.ACTIVE
        );

        propertyRepository.save(property);
    }
}