package ma.logitrack.fleet.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.logitrack.fleet.domain.DriverStatus;
import ma.logitrack.fleet.service.DriverService;
import ma.logitrack.fleet.web.dto.ChangeAvailabilityRequest;
import ma.logitrack.fleet.web.dto.CreateDriverRequest;
import ma.logitrack.fleet.web.dto.DriverResponse;
import ma.logitrack.fleet.web.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody CreateDriverRequest request) {
        DriverResponse created = driverService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public PageResponse<DriverResponse> search(
            @RequestParam(required = false) DriverStatus status,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        return driverService.search(status, pageable);
    }

    @GetMapping("/{id}")
    public DriverResponse findById(@PathVariable UUID id) {
        return driverService.findById(id);
    }

    @PatchMapping("/{id}/availability")
    public DriverResponse changeAvailability(@PathVariable UUID id, @Valid @RequestBody ChangeAvailabilityRequest request) {
        return driverService.setAvailability(id, request.status());
    }
}
