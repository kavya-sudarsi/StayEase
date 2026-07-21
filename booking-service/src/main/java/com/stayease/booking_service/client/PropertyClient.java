package com.stayease.booking_service.client;

import com.stayease.booking_service.dto.RoomDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.stayease.booking_service.dto.PropertyDetailsResponse;

@FeignClient(
        name = "property-service",
        url = "${property.service.url}"
)
public interface PropertyClient {

    @PutMapping("/properties/rooms/{roomId}/decrease")
    void decreaseAvailableBeds(@PathVariable("roomId") Long roomId
    );

    @PutMapping("/properties/rooms/{roomId}/increase")
    void increaseAvailableBeds(@PathVariable("roomId") Long roomId
    );

    @GetMapping("/properties/rooms/{roomId}")
    RoomDetailsResponse getRoom(@PathVariable("roomId") Long roomId
    );

    @GetMapping("/properties/{propertyId}")
    PropertyDetailsResponse getProperty(
            @PathVariable("propertyId") Long propertyId
    );
}