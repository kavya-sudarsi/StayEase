package com.stayease.property_service.service;

import com.stayease.property_service.dto.RoomRequest;
import com.stayease.property_service.entity.Property;
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
    public Room addRoom(Long propertyId,
                        RoomRequest request,
                        String email) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Property not found"
                        ));

        // OWNER VALIDATION
        if (!property.getOwnerEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not owner of this property"
            );
        }

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .totalBeds(request.getTotalBeds())
                .availableBeds(request.getTotalBeds())
                .pricePerBed(request.getPricePerBed())
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

    // DELETE ROOM
    @Transactional
    public void deleteRoom(Long roomId, String email) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room not found"
                        ));

        // OWNER VALIDATION
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
    }
}