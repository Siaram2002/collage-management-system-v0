package com.cms.transport.bus.service;


import com.cms.transport.bus.dto.BusRequest;
import com.cms.transport.bus.dto.BusResponse;

import java.util.List;

public interface BusService {

    BusResponse createBus(BusRequest request);

    BusResponse getBus(Long busId);

    List<BusResponse> getAllBuses();

    BusResponse updateBus(Long busId, BusRequest request);

    void deleteBus(Long busId);
}

