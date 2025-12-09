package com.cms.transport.bus.service;

import com.cms.transport.bus.models.GPSDevice;

import java.util.List;

public interface GPSDeviceService {

    GPSDevice registerDevice(GPSDevice device);

    GPSDevice updateDevice(Long id, GPSDevice device);

    GPSDevice getDevice(Long id);

    List<GPSDevice> getAllDevices();

    boolean deleteDevice(Long id);

    GPSDevice updateHealth(Long id, Integer battery, Boolean ignition, String pingTime);
}
