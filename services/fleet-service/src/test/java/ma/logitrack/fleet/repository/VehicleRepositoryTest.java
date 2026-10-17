package ma.logitrack.fleet.repository;

import ma.logitrack.fleet.TestcontainersConfiguration;
import ma.logitrack.fleet.domain.Vehicle;
import ma.logitrack.fleet.domain.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository repository;

    @Test
    void shouldFindByStatus() {
        Vehicle inMaintenance = Vehicle.create("90101-A-1", "Iveco Daily", 2000);
        inMaintenance.changeStatus(VehicleStatus.MAINTENANCE);
        repository.saveAndFlush(inMaintenance);
        repository.saveAndFlush(Vehicle.create("90102-A-1", "Iveco Daily", 2000));

        var page = repository.findByStatus(VehicleStatus.MAINTENANCE, PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(Vehicle::getPlate)
                .contains("90101-A-1")
                .doesNotContain("90102-A-1");
    }

    @Test
    void shouldEnforceUniquePlate() {
        repository.saveAndFlush(Vehicle.create("90100-A-1", "Iveco Daily", 2000));

        assertThatThrownBy(() -> repository.saveAndFlush(Vehicle.create("90100-A-1", "Fiat Ducato", 1400)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
