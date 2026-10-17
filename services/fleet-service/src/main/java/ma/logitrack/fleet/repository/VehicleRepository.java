package ma.logitrack.fleet.repository;

import ma.logitrack.fleet.domain.Vehicle;
import ma.logitrack.fleet.domain.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    boolean existsByPlate(String plate);

    boolean existsByCurrentDriverId(UUID driverId);

    Page<Vehicle> findByStatus(VehicleStatus status, Pageable pageable);
}
