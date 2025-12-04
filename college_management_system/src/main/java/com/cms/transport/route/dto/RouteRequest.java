package com.cms.transport.route.dto;

import com.cms.common.enums.Status;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RouteRequest {
    private String routeName;
    private String description;
    private Status status;
}
