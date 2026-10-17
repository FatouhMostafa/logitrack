package ma.logitrack.fleet.web.dto;

import ma.logitrack.fleet.domain.VehicleStatus;

import java.time.Instant;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String plate,
        String model,
        int capacityKg,
        VehicleStatus status,
        UUID currentDriverId,
        UUID currentDeliveryId,
        Instant createdAt,
        Instant updatedAt
) {
}
