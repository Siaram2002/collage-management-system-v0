package com.cms.transport.driver.mapper;

import com.cms.transport.driver.dto.QRScanLogTableDTO;
import com.cms.transport.driver.model.QRScanLog;

import java.time.format.DateTimeFormatter;

public class QRScanLogTableMapper {



    public static QRScanLogTableDTO toDTO(QRScanLog log) {

        return QRScanLogTableDTO.builder()

                // PHOTO
                .photo(
                        log.getStudent().getPhotoUrl() != null
                                ? log.getStudent().getPhotoUrl()
                                : "/images/default-user.png"
                )

                // STUDENT NAME
                .studentName(
                        log.getStudent().getFirstName() + " " +
                                log.getStudent().getLastName()
                )

                // ROLL NUMBER
                .rollNo(log.getStudent().getRollNumber())

                // BUS ROUTE
                .busRoute(
                        log.getRoute() != null
                                ? log.getRoute().getRouteName()
                                : "-"
                )

                // SCAN TIME
                .scanTime(
                        log.getScannedAt()
                                .format(DateTimeFormatter.ofPattern("HH:mm"))
                )

                // LOCATION
                .location(log.getLocationAddress())

                // STATUS
                .status(log.getScanStatus())

                .build();
    }
}
