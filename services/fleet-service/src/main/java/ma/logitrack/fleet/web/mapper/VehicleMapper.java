package ma.logitrack.fleet.web.mapper;

import ma.logitrack.fleet.domain.Vehicle;
import ma.logitrack.fleet.web.dto.VehicleResponse;

public final class VehicleMapper {

    private VehicleMapper() {
    }

    public static VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlate(),
                vehicle.getModel(),
                vehicle.getCapacityKg(),
                vehicle.getStatus(),
                vehicle.getCurrentDriverId(),
                vehicle.getCurrentDeliveryId(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt());
    }
}
