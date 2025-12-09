package com.cms.busPass;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_bus_pass", indexes = {
        @Index(name = "idx_latest_buspass_roll_adm", columnList = "rollNumber,admissionNumber")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentBusPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student identifiers
    @Column(nullable = false, length = 20)
    private String rollNumber;

    @Column(nullable = false, length = 20)
    private String admissionNumber;

    // Points to the latest active bus pass
    @Column(nullable = false, length = 36)
    private String busPassUid;
}
