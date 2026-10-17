package ma.logitrack.fleet.service;

import lombok.RequiredArgsConstructor;
import ma.logitrack.fleet.domain.Driver;
import ma.logitrack.fleet.domain.Vehicle;
import ma.logitrack.fleet.domain.VehicleStatus;
import ma.logitrack.fleet.domain.VehicleStatusPolicy;
import ma.logitrack.fleet.domain.exception.BusinessRuleException;
import ma.logitrack.fleet.domain.exception.DuplicateResourceException;
import ma.logitrack.fleet.domain.exception.ResourceNotFoundException;
import ma.logitrack.fleet.repository.DriverRepository;
import ma.logitrack.fleet.repository.VehicleRepository;
import ma.logitrack.fleet.web.dto.CreateVehicleRequest;
import ma.logitrack.fleet.web.dto.PageResponse;
import ma.logitrack.fleet.web.dto.UpdateVehicleRequest;
import ma.logitrack.fleet.web.dto.VehicleResponse;
import ma.logitrack.fleet.web.mapper.VehicleMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicles;
    private final DriverRepository drivers;

    @Transactional
    public VehicleResponse create(CreateVehicleRequest request) {
        if (vehicles.existsByPlate(request.plate())) {
            throw new DuplicateResourceException("A vehicle with the license plate " + request.plate() + " already exists");
        }
        Vehicle vehicle = Vehicle.create(request.plate(), request.model(), request.capacityKg());
        return VehicleMapper.toResponse(vehicles.save(vehicle));
    }

    public VehicleResponse findById(UUID id) {
        return VehicleMapper.toResponse(getVehicle(id));
    }

    public PageResponse<VehicleResponse> search(VehicleStatus status, Pageable pageable) {
        Page<Vehicle> page = (status == null)
                ? vehicles.findAll(pageable)
                : vehicles.findByStatus(status, pageable);
        return PageResponse.from(page.map(VehicleMapper::toResponse));
    }

    @Transactional
    public VehicleResponse update(UUID id, UpdateVehicleRequest request) {
        Vehicle vehicle = getVehicle(id);
        vehicle.updateDetails(request.model(), request.capacityKg());
        return VehicleMapper.toResponse(vehicle);
    }

    @Transactional
    public VehicleResponse changeStatus(UUID id, VehicleStatus target) {
        Vehicle vehicle = getVehicle(id);

        if (!VehicleStatusPolicy.isManualTransitionAllowed(
                vehicle.getStatus(), target)) {
            throw new BusinessRuleException(
                    "Transition " + vehicle.getStatus()
                            + " → " + target + " is not allowed");
        }

        if (target == VehicleStatus.MAINTENANCE
                || target == VehicleStatus.OUT_OF_SERVICE) {
            ensureNoPairedDriver(vehicle);
        }

        vehicle.changeStatus(target);

        return VehicleMapper.toResponse(vehicle);
    }

    @Transactional
    public void delete(UUID id) {
        Vehicle vehicle = getVehicle(id);
        if (vehicle.isOnMission()) {
            throw new BusinessRuleException("Cannot delete a vehicle that is on a mission");
        }
        vehicles.delete(vehicle);
    }

    @Transactional
    public VehicleResponse pairDriver(UUID vehicleId, UUID driverId) {
        Vehicle vehicle = getVehicle(vehicleId);
        Driver driver = drivers.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", driverId));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new BusinessRuleException("The vehicle must be available to be paired");
        }
        if (vehicle.hasDriver()) {
            throw new BusinessRuleException("The vehicle already has a driver");
        }
        if (!driver.isAvailable()) {
            throw new BusinessRuleException("The driver must be available");
        }
        if (vehicles.existsByCurrentDriverId(driverId)) {
            throw new BusinessRuleException("This driver is already paired with another vehicle");
        }
        vehicle.pairWith(driverId);
        return VehicleMapper.toResponse(vehicle);
    }

    @Transactional
    public VehicleResponse unpairDriver(UUID vehicleId) {
        Vehicle vehicle = getVehicle(vehicleId);
        if (vehicle.isOnMission()) {
            throw new BusinessRuleException("Cannot unpair a vehicle that is on a mission");
        }
        if (!vehicle.hasDriver()) {
            throw new BusinessRuleException("This vehicle does not have a driver");
        }
        vehicle.unpair();
        return VehicleMapper.toResponse(vehicle);
    }

    private void ensureNoPairedDriver(Vehicle vehicle) {
        if (vehicle.hasDriver()) {
            throw new BusinessRuleException(
                    "Cannot change the status of a paired vehicle");
        }
    }

    private Vehicle getVehicle(UUID id) {
        return vehicles.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }
}
