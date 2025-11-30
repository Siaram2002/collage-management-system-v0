package com.cms.transport.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_logs",
       indexes = {
           @Index(name = "idx_event_entity_time", columnList = "entityName, entityId, timestamp"),
           @Index(name = "idx_event_type_time", columnList = "eventType, timestamp")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    // Example: "BusAssignment", "Driver", "GPSLog"
    private String entityName;

    // id of the row changed (assignmentId / driverId / busId)
    private Long entityId;

    // Example: "ASSIGNMENT_CREATED", "DRIVER_LEAVE", "FIELD_UPDATED"
    private String eventType;

    // Optional: name of the field changed (for field-level audit)
    private String fieldName;

    // Old and new values (store as stringified JSON or plain text)
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    // Who triggered (system / admin username / service)
    private String performedBy;

    @CreationTimestamp
    private LocalDateTime timestamp;

    // Optional: extra context (trip id, gps id, etc.)
    @Column(columnDefinition = "TEXT")
    private String details;
}
