package ma.logitrack.fleet.repository;

import ma.logitrack.fleet.domain.Driver;
import ma.logitrack.fleet.domain.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {

    boolean existsByLicenseNumber(String licenseNumber);

    Page<Driver> findByStatus(DriverStatus status, Pageable pageable);
}
