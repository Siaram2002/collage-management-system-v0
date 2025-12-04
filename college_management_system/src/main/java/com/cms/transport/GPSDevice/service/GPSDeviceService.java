package com.cms.transport.GPSDevice.service;

import com.cms.transport.GPSDevice.dto.GPSDeviceRequest;
import com.cms.transport.GPSDevice.dto.GPSDeviceResponse;
import com.cms.transport.GPSDevice.dto.GPSPingUpdateRequest;

import java.util.List;

public interface GPSDeviceService {

    GPSDeviceResponse register(GPSDeviceRequest dto);

    GPSDeviceResponse update(Long gpsId, GPSDeviceRequest dto);

    GPSDeviceResponse getById(Long gpsId);

    List<GPSDeviceResponse> getAll();

    void delete(Long gpsId);

    GPSDeviceResponse updatePing(Long gpsId, GPSPingUpdateRequest dto);
}
