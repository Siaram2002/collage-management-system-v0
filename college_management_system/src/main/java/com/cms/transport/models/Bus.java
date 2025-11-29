package com.cms.transport.models;

import com.cms.college.models.Contact;
import com.cms.transport.enums.BusStatus;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "buses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long busId;

    @Column(nullable = false, unique = true)
    private String busNumber;

    private String registrationNumber;
    private Integer seatingCapacity;

    @Enumerated(EnumType.STRING)
    private BusStatus status = BusStatus.INACTIVE;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "gps_id")
    private GPSDevice gpsDevice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
