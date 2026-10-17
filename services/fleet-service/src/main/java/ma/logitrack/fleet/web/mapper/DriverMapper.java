package ma.logitrack.fleet.web.mapper;

import ma.logitrack.fleet.domain.Driver;
import ma.logitrack.fleet.web.dto.DriverResponse;

public final class DriverMapper {

    private DriverMapper() {
    }

    public static DriverResponse toResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getPhone(),
                driver.getLicenseNumber(),
                driver.getStatus(),
                driver.getCreatedAt());
    }
}
