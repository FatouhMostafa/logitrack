package ma.logitrack.fleet.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PairDriverRequest(@NotNull UUID driverId) {
}
