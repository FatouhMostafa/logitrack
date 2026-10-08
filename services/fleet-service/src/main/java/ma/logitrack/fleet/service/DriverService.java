package ma.logitrack.fleet.service;

import lombok.RequiredArgsConstructor;
import ma.logitrack.fleet.domain.Driver;
import ma.logitrack.fleet.domain.DriverStatus;
import ma.logitrack.fleet.domain.exception.BusinessRuleException;
import ma.logitrack.fleet.domain.exception.DuplicateResourceException;
import ma.logitrack.fleet.domain.exception.ResourceNotFoundException;
import ma.logitrack.fleet.repository.DriverRepository;
import ma.logitrack.fleet.web.dto.CreateDriverRequest;
import ma.logitrack.fleet.web.dto.DriverResponse;
import ma.logitrack.fleet.web.dto.PageResponse;
import ma.logitrack.fleet.web.mapper.DriverMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository drivers;

    @Transactional
    public DriverResponse create(CreateDriverRequest request) {
        if (drivers.existsByLicenseNumber(request.licenseNumber())) {
            throw new DuplicateResourceException("A driver with the license number " + request.licenseNumber() + " already exists");
        }
        Driver driver = Driver.create(request.firstName(), request.lastName(), request.phone(), request.licenseNumber());
        return DriverMapper.toResponse(drivers.save(driver));
    }

    public DriverResponse findById(UUID id) {
        return DriverMapper.toResponse(getDriver(id));
    }

    public PageResponse<DriverResponse> search(DriverStatus status, Pageable pageable) {
        Page<Driver> page = (status == null)
                ? drivers.findAll(pageable)
                : drivers.findByStatus(status, pageable);
        return PageResponse.from(page.map(DriverMapper::toResponse));
    }

    @Transactional
    public DriverResponse setAvailability(UUID id, DriverStatus target) {
        if (target == DriverStatus.ON_MISSION) {
            throw new BusinessRuleException("The ON_MISSION status is managed automatically by deliveries");
        }
        Driver driver = getDriver(id);
        if (driver.getStatus() == DriverStatus.ON_MISSION) {
            throw new BusinessRuleException("Cannot change the availability of a driver who is on a mission");
        }
        driver.changeAvailability(target);
        return DriverMapper.toResponse(driver);
    }

    private Driver getDriver(UUID id) {
        return drivers.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
    }
}
