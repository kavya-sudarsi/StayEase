package com.stayease.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "property-service")
public interface PropertyClient {

    @PutMapping("/properties/rooms/{roomId}/decrease")
    void decreaseAvailableBeds(@PathVariable("roomId") Long roomId);

    @PutMapping("/properties/rooms/{roomId}/increase")
    void increaseAvailableBeds(@PathVariable("roomId") Long roomId);
}