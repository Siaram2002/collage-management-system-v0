package com.cms.transport.bus.serviceImpl;


import com.cms.transport.GPSDevice.models.GPSDevice;
import com.cms.transport.GPSDevice.repository.GPSDeviceRepository;

import com.cms.transport.bus.dto.BusRequest;
import com.cms.transport.bus.dto.BusResponse;
import com.cms.transport.bus.enums.BusStatus;
import com.cms.transport.bus.exception.AlreadyExistsException;
import com.cms.transport.bus.exception.NotFoundException;
import com.cms.transport.bus.model.Bus;
import com.cms.transport.bus.repository.BusRepository;
import com.cms.transport.bus.service.BusService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {

    private final BusRepository busRepo;
    private final GPSDeviceRepository gpsRepo;

    @Override
    public BusResponse createBus(BusRequest request) {

        if (busRepo.existsByBusNumber(request.getBusNumber())) {
            throw new AlreadyExistsException("Bus number already exists");
        }

        GPSDevice gps = null;
        if (request.getGpsId() != null) {
            gps = gpsRepo.findById(request.getGpsId())
                    .orElseThrow(() -> new NotFoundException("GPS Device not found"));
        }

        Bus bus = Bus.builder()
                .busNumber(request.getBusNumber())
                .registrationNumber(request.getRegistrationNumber())
                .seatingCapacity(request.getSeatingCapacity())
                .status(BusStatus.ACTIVE)
                .gpsDevice(gps)
                .build();

        Bus saved = busRepo.save(bus);

        return convertToResponse(saved);
    }

    @Override
    public BusResponse getBus(Long busId) {
        Bus bus = busRepo.findById(busId)
                .orElseThrow(() -> new NotFoundException("Bus not found"));

        return convertToResponse(bus);
    }

    @Override
    public List<BusResponse> getAllBuses() {
        return busRepo.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public BusResponse updateBus(Long busId, BusRequest request) {

        Bus bus = busRepo.findById(busId)
                .orElseThrow(() -> new NotFoundException("Bus not found"));

        if (!bus.getBusNumber().equals(request.getBusNumber()) &&
                busRepo.existsByBusNumber(request.getBusNumber())) {
            throw new AlreadyExistsException("Bus number already exists");
        }

        GPSDevice gps = null;
        if (request.getGpsId() != null) {
            gps = gpsRepo.findById(request.getGpsId())
                    .orElseThrow(() -> new NotFoundException("GPS device not found"));
        }

        bus.setBusNumber(request.getBusNumber());
        bus.setRegistrationNumber(request.getRegistrationNumber());
        bus.setSeatingCapacity(request.getSeatingCapacity());
        bus.setGpsDevice(gps);

        Bus updated = busRepo.save(bus);

        return convertToResponse(updated);
    }

    @Override
    public void deleteBus(Long busId) {
        Bus bus = busRepo.findById(busId)
                .orElseThrow(() -> new NotFoundException("Bus not found"));

        bus.setStatus(BusStatus.INACTIVE);
        busRepo.save(bus);
    }

    private BusResponse convertToResponse(Bus bus) {
        return BusResponse.builder()
                .busId(bus.getBusId())
                .busNumber(bus.getBusNumber())
                .registrationNumber(bus.getRegistrationNumber())
                .seatingCapacity(bus.getSeatingCapacity())
                .status(bus.getStatus())
                .gpsId(bus.getGpsDevice() != null ? bus.getGpsDevice().getGpsId() : null)
                .build();
    }
}

