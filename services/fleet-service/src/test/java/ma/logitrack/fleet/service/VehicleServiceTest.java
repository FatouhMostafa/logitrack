package ma.logitrack.fleet.service;

import ma.logitrack.fleet.domain.Driver;
import ma.logitrack.fleet.domain.Vehicle;
import ma.logitrack.fleet.domain.VehicleStatus;
import ma.logitrack.fleet.domain.exception.BusinessRuleException;
import ma.logitrack.fleet.domain.exception.DuplicateResourceException;
import ma.logitrack.fleet.domain.exception.ResourceNotFoundException;
import ma.logitrack.fleet.repository.DriverRepository;
import ma.logitrack.fleet.repository.VehicleRepository;
import ma.logitrack.fleet.web.dto.CreateVehicleRequest;
import ma.logitrack.fleet.web.dto.VehicleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicles;

    @Mock
    private DriverRepository drivers;

    private VehicleService service;

    private final UUID vehicleId = UUID.randomUUID();
    private final UUID driverId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new VehicleService(vehicles, drivers);
    }

    @Test
    void shouldCreateVehicle_whenPlateIsUnique() {
        when(vehicles.existsByPlate("12345-A-6")).thenReturn(false);
        when(vehicles.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse response = service.create(new CreateVehicleRequest("12345-A-6", "Renault Master", 1500));

        assertThat(response.plate()).isEqualTo("12345-A-6");
        assertThat(response.status()).isEqualTo(VehicleStatus.AVAILABLE);
    }

    @Test
    void shouldThrowDuplicate_whenPlateExists() {
        when(vehicles.existsByPlate("12345-A-6")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateVehicleRequest("12345-A-6", "Renault Master", 1500)))
                .isInstanceOf(DuplicateResourceException.class);
        verify(vehicles, never()).save(any());
    }

    @Test
    void shouldThrowNotFound_whenIdUnknown() {
        when(vehicles.findById(vehicleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(vehicleId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldChangeStatus_whenTransitionAllowed() {
        Vehicle vehicle = availableVehicle();
        when(vehicles.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        VehicleResponse response = service.changeStatus(vehicleId, VehicleStatus.MAINTENANCE);

        assertThat(response.status()).isEqualTo(VehicleStatus.MAINTENANCE);
    }

    @Test
    void shouldRejectStatusChange_whenTargetIsOnMission() {
        when(vehicles.findById(vehicleId)).thenReturn(Optional.of(availableVehicle()));

        assertThatThrownBy(() -> service.changeStatus(vehicleId, VehicleStatus.ON_MISSION))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldRejectDelete_whenVehicleOnMission() {
        Vehicle vehicle = onMissionVehicle();
        when(vehicles.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> service.delete(vehicleId))
                .isInstanceOf(BusinessRuleException.class);
        verify(vehicles, never()).delete(any());
    }

    @Test
    void shouldDelete_whenVehicleAvailable() {
        Vehicle vehicle = availableVehicle();
        when(vehicles.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        service.delete(vehicleId);

        verify(vehicles).delete(vehicle);
    }

    @Test
    void shouldPairDriver_whenBothAvailable() {
        when(vehicles.findById(vehicleId)).thenReturn(Optional.of(availableVehicle()));
        when(drivers.findById(driverId)).thenReturn(Optional.of(availableDriver()));
        when(vehicles.existsByCurrentDriverId(driverId)).thenReturn(false);

        VehicleResponse response = service.pairDriver(vehicleId, driverId);

        assertThat(response.currentDriverId()).isEqualTo(driverId);
    }

    @Test
    void shouldRejectPairing_whenDriverAlreadyPaired() {
        when(vehicles.findById(vehicleId)).thenReturn(Optional.of(availableVehicle()));
        when(drivers.findById(driverId)).thenReturn(Optional.of(availableDriver()));
        when(vehicles.existsByCurrentDriverId(driverId)).thenReturn(true);

        assertThatThrownBy(() -> service.pairDriver(vehicleId, driverId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already paired");
    }

    @Test
    void shouldRejectPairing_whenVehicleInMaintenance() {
        Vehicle vehicle = availableVehicle();
        vehicle.changeStatus(VehicleStatus.MAINTENANCE);
        when(vehicles.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(drivers.findById(driverId)).thenReturn(Optional.of(availableDriver()));

        assertThatThrownBy(() -> service.pairDriver(vehicleId, driverId))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldRejectUnpair_whenVehicleOnMission() {
        Vehicle vehicle = onMissionVehicle();
        vehicle.pairWith(driverId);

        when(vehicles.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> service.unpairDriver(vehicleId))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(vehicle.getCurrentDriverId()).isEqualTo(driverId);
    }

    @Test
    void shouldRejectMaintenance_whenDriverPaired() {
        Vehicle vehicle = availableVehicle();
        vehicle.pairWith(driverId);

        when(vehicles.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() ->
                service.changeStatus(vehicleId, VehicleStatus.MAINTENANCE))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(vehicle.getStatus())
                .isEqualTo(VehicleStatus.AVAILABLE);
    }

    private Vehicle availableVehicle() {
        return Vehicle.create("12345-A-6", "Renault Master", 1500);
    }

    private Driver availableDriver() {
        return Driver.create("Youssef", "Bennani", "+212610000001", "CAS-000001");
    }

    private Vehicle onMissionVehicle() {
        Vehicle vehicle = availableVehicle();
        vehicle.changeStatus(VehicleStatus.ON_MISSION);
        return vehicle;
    }
}
