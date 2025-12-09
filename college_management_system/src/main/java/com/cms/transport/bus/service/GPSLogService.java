package com.cms.transport.bus.service;

import java.time.LocalDateTime;
import java.util.List;

import com.cms.transport.bus.dto.GPSLogDTO;
import com.cms.transport.bus.models.GPSLog;

public interface GPSLogService {

    GPSLog saveLog(GPSLog log);

    GPSLog addLog(GPSLogDTO dto);

    GPSLog getLatestLog(Long gpsId);

    List<GPSLog> getHistory(Long gpsId, LocalDateTime start, LocalDateTime end);
}
