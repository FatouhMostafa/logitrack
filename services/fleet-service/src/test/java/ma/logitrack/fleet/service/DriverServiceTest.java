package ma.logitrack.fleet.service;

import ma.logitrack.fleet.domain.Driver;
import ma.logitrack.fleet.domain.DriverStatus;
import ma.logitrack.fleet.domain.exception.BusinessRuleException;
import ma.logitrack.fleet.domain.exception.DuplicateResourceException;
import ma.logitrack.fleet.repository.DriverRepository;
import ma.logitrack.fleet.web.dto.CreateDriverRequest;
import ma.logitrack.fleet.web.dto.DriverResponse;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository drivers;

    private DriverService service;

    private final UUID driverId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new DriverService(drivers);
    }

    @Test
    void shouldThrowDuplicate_whenLicenseNumberExists() {
        when(drivers.existsByLicenseNumber("CAS-000001")).thenReturn(true);

        assertThatThrownBy(() -> service.create(
                new CreateDriverRequest("Youssef", "Bennani", "+212610000001", "CAS-000001")))
                .isInstanceOf(DuplicateResourceException.class);
        verify(drivers, never()).save(any());
    }

    @Test
    void shouldReject_whenTargetIsOnMission() {
        assertThatThrownBy(() -> service.setAvailability(driverId, DriverStatus.ON_MISSION))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldRejectAvailabilityChange_whenDriverOnMission() {
        Driver driver = Driver.create("Salma", "El Idrissi", "+212610000002", "CAS-000002");
        driver.changeAvailability(DriverStatus.ON_MISSION);

        when(drivers.findById(driverId)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() ->
                service.setAvailability(driverId, DriverStatus.OFF_DUTY))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(driver.getStatus()).isEqualTo(DriverStatus.ON_MISSION);
    }

    @Test
    void shouldSetOffDuty_whenDriverAvailable() {
        Driver driver = Driver.create("Karim", "Alaoui", "+212610000003", "CAS-000003");
        when(drivers.findById(driverId)).thenReturn(Optional.of(driver));

        DriverResponse response = service.setAvailability(driverId, DriverStatus.OFF_DUTY);

        assertThat(response.status()).isEqualTo(DriverStatus.OFF_DUTY);
    }
}
