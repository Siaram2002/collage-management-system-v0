package com.cms.transport.models;

import com.cms.college.models.Contact;
import com.cms.common.enums.Status;

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

public class Bus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long busId;

	private String busNumber;
	private String registrationNumber;
	private Integer seatingCapacity;

	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact_id")
	private Contact contact;

	@OneToOne(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
	private GPSDevice gpsDevice;
}
