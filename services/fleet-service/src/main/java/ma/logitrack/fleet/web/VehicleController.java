package ma.logitrack.fleet.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.logitrack.fleet.domain.VehicleStatus;
import ma.logitrack.fleet.service.VehicleService;
import ma.logitrack.fleet.web.dto.ChangeVehicleStatusRequest;
import ma.logitrack.fleet.web.dto.CreateVehicleRequest;
import ma.logitrack.fleet.web.dto.PageResponse;
import ma.logitrack.fleet.web.dto.PairDriverRequest;
import ma.logitrack.fleet.web.dto.UpdateVehicleRequest;
import ma.logitrack.fleet.web.dto.VehicleResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody CreateVehicleRequest request) {
        VehicleResponse created = vehicleService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public PageResponse<VehicleResponse> search(
            @RequestParam(required = false) VehicleStatus status,
            @PageableDefault(size = 20, sort = "plate") Pageable pageable) {
        return vehicleService.search(status, pageable);
    }

    @GetMapping("/{id}")
    public VehicleResponse findById(@PathVariable UUID id) {
        return vehicleService.findById(id);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateVehicleRequest request) {
        return vehicleService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public VehicleResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeVehicleStatusRequest request) {
        return vehicleService.changeStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/driver")
    public VehicleResponse pairDriver(@PathVariable UUID id, @Valid @RequestBody PairDriverRequest request) {
        return vehicleService.pairDriver(id, request.driverId());
    }

    @DeleteMapping("/{id}/driver")
    public VehicleResponse unpairDriver(@PathVariable UUID id) {
        return vehicleService.unpairDriver(id);
    }
}
