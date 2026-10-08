package ma.logitrack.fleet.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateVehicleRequest(
        @NotBlank
        @Pattern(regexp = "^[0-9]{1,6}-[A-Z]-[0-9]{1,2}$", message = "Expected format: 12345-A-6")
        String plate,

        @NotBlank @Size(max = 60)
        String model,

        @NotNull @Positive
        Integer capacityKg
) {
}
