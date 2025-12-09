package com.cms.transport.bus.models;

import com.cms.college.models.Contact;
import com.cms.transport.bus.enums.BusStatus;
import com.cms.transport.models.TransportAssignment;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "buses", indexes = { @Index(name = "idx_bus_number", columnList = "bus_number") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long busId;

	@Column(name = "bus_number", nullable = false, unique = true, length = 50)
	private String busNumber;

	@Column(name = "registration_number", length = 80)
	private String registrationNumber;

	@Column(name = "seating_capacity")
	private Integer seatingCapacity;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 20)
	private BusStatus status = BusStatus.INACTIVE;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "gps_id")
	@JsonManagedReference
	private GPSDevice gpsDevice;

	@OneToOne(mappedBy = "bus", fetch = FetchType.LAZY)
	@JsonManagedReference(value = "bus-assignment")
	private TransportAssignment assignment;


	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
