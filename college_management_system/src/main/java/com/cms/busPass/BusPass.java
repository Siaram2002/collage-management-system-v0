package com.cms.busPass;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;



@Entity
@Table(name = "bus_pass", indexes = {
        @Index(name = "idx_bus_pass_uid", columnList = "busPassUid"),
        @Index(name = "idx_roll_admission", columnList = "rollNumber,admissionNumber")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique bus pass identifier for QR scanning
    @Column(nullable = false, unique = true, length = 36)
    private String busPassUid = UUID.randomUUID().toString();

    // Student identifiers (denormalized for history)
    @Column(nullable = false, length = 20)
    private String rollNumber;

    @Column(nullable = false, length = 20)
    private String admissionNumber;

    private String studentName;
    private String course;
    private String department;
    
    private int routeId;

    // Student image URL
    @Column(length = 255)
    private String studentPhotoUrl;

    private String qrUrl;

//    private LocalDateTime issuedAt;
//    private LocalDateTime validTill;


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime issuedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime validTill;


    @Enumerated(EnumType.STRING)
    private BusPassStatus status = BusPassStatus.ACTIVE;

    private BigDecimal feePaid;
    private String busRoute;
    private String routeCode;
}
