package ma.logitrack.fleet.web.dto;

import jakarta.validation.constraints.NotNull;
import ma.logitrack.fleet.domain.DriverStatus;

public record ChangeAvailabilityRequest(@NotNull DriverStatus status) {
}
