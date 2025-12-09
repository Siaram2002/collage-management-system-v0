package com.cms.college.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cms.common.enums.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_master")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    // User is optional now
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

 

    @Column(length = 255)
    private String photoUrl;
    
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status adminStatus = Status.ACTIVE;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
