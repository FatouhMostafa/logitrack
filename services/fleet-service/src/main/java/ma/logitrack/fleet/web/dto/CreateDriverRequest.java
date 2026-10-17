package ma.logitrack.fleet.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateDriverRequest(
        @NotBlank @Size(max = 60) String firstName,
        @NotBlank @Size(max = 60) String lastName,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Invalid phone number") String phone,
        @NotBlank @Size(max = 30) String licenseNumber
) {
}
