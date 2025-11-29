package com.cms.transport.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.cms.transport.enums.EventType;
import com.cms.transport.enums.ModuleName;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "event_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private ModuleName module;

    // Name of the entity/table (Bus, Driver, GPSDevice, Assignment)
    private String entityName;

    // Id of record changed
    private Long recordId;

    // Optional additional info
    private String description;

    // User who performed action
    private Long performedByUserId;

    // On which device or system  
    private String source; // Web, Mobile, Backend, CRON, GPS, API etc.

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Relationship to field-level logs
    @OneToMany(mappedBy = "eventLog", cascade = CascadeType.ALL)
    private List<EventLogDetail> details;
}
