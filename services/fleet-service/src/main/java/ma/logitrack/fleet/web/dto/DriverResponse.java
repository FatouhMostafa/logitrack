package ma.logitrack.fleet.web.dto;

import ma.logitrack.fleet.domain.DriverStatus;

import java.time.Instant;
import java.util.UUID;

public record DriverResponse(
        UUID id,
        String firstName,
        String lastName,
        String phone,
        String licenseNumber,
        DriverStatus status,
        Instant createdAt
) {
}
