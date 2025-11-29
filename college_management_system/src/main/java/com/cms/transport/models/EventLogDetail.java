package com.cms.transport.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_log_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLogDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_log_id", nullable = false)
    private EventLog eventLog;

    private String fieldName;
    private String oldValue;
    private String newValue;
}
