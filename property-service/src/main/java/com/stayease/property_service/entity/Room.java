package com.stayease.property_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"room_number", "property_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomNumber;

    // 1-sharing / 2-sharing / 3-sharing
    private String roomType;

    @Min(1)
    private int totalBeds;

    @Min(0)
    private int availableBeds;

    private Double pricePerBed;

    private boolean wifi;

    private boolean ac;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
}