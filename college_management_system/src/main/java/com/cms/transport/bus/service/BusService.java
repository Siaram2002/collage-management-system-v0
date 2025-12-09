package com.cms.transport.bus.service;



import com.cms.transport.bus.dto.BusLocationDTO;
import com.cms.transport.bus.models.Bus;

import java.util.List;

public interface BusService {

    Bus createBus(Bus bus);

    Bus updateBus(Long busId, Bus bus);

    Bus getBus(Long busId);

    List<Bus> getAllBuses();

    boolean deleteBus(Long busId);

    Bus assignGPS(Long busId, Long gpsId);

    Bus removeGPS(Long busId);
    
    BusLocationDTO getLatestBusLocation(Long busId);
    
    List<BusLocationDTO> getAllBusesLatestLocations();
}
