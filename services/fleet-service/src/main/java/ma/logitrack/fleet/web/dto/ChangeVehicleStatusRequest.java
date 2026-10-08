package ma.logitrack.fleet.web.dto;

import jakarta.validation.constraints.NotNull;
import ma.logitrack.fleet.domain.VehicleStatus;

public record ChangeVehicleStatusRequest(@NotNull VehicleStatus status) {
}
