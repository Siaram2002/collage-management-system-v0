package com.cms.transport.driver.model;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cms.college.models.Contact;
import com.cms.college.models.User;
import com.cms.transport.driver.enums.DriverStatus;
import com.cms.transport.models.TransportAssignment;
import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name = "drivers", uniqueConstraints = @UniqueConstraint(columnNames = "licenseNumber"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    @Column(nullable = false)
    private LocalDate licenseExpiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status = DriverStatus.INACTIVE;

    // Do not serialize user account to avoid circular references
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User userAccount;

    // Contact + Address is safe to serialize (no circular references)
    @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    // Prevents circular references with TransportAssignment
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "driver", cascade = CascadeType.ALL)
    @JsonIgnore
    private TransportAssignment assignment;

    @Column(length = 512)
    private String photoUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
