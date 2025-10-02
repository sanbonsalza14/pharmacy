package com.my.pharmacy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pharmacy",
        uniqueConstraints = @UniqueConstraint(name = "uk_place_lat_lng", columnNames = {"name","latitude","longitude"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pharmacy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=200)
    private String name;

    @Column(nullable=false, length=300)
    private String address;

    @Column(nullable=false)
    private double latitude;

    @Column(nullable=false)
    private double longitude;

    @Column(nullable=false)
    private double distance;
}
