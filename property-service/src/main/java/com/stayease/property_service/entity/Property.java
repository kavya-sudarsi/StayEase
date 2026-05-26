package com.stayease.property_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BASIC INFO

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String city;

    private String state;

    private String pincode;

    @Column(length = 1000)
    private String description;

    // PROPERTY DETAILS

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    private GenderType gender;

    private String contactNumber;

    private String imageUrl;

    // AMENITIES

    @ElementCollection
    @CollectionTable(
            name = "property_amenities",
            joinColumns = @JoinColumn(name = "property_id")
    )
    @Column(name = "amenity")
    private List<String> amenities;

    // STATUS

    @Enumerated(EnumType.STRING)
    private PropertyStatus status;

    private LocalDateTime createdAt;

    // OWNER EMAIL

    @Column(nullable = false)
    private String ownerEmail;

    // ROOMS

    @JsonManagedReference
    @OneToMany(
            mappedBy = "property",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Room> rooms;
}