package com.cms.transport.models;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cms.college.models.Address;
import com.cms.college.models.Contact;
import com.cms.college.models.User;

import com.cms.common.enums.Status;
import com.cms.transport.enums.DriverStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity
@Table(name = "drivers", uniqueConstraints = { @UniqueConstraint(columnNames = "licenseNumber") })
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
	private DriverStatus status = DriverStatus.INACTIVE;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User userAccount;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact_id")
	private Contact contact;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private Address address;

	private String photoUrl;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
