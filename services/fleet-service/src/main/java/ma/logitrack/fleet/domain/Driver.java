package ma.logitrack.fleet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "drivers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "license_number", nullable = false, unique = true, length = 30)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DriverStatus status;

    @Version
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static Driver create(String firstName, String lastName, String phone, String licenseNumber) {
        Driver driver = new Driver();
        driver.firstName = firstName;
        driver.lastName = lastName;
        driver.phone = phone;
        driver.licenseNumber = licenseNumber;
        driver.status = DriverStatus.AVAILABLE;
        return driver;
    }

    public void changeAvailability(DriverStatus newStatus) {
        this.status = newStatus;
    }

    public boolean isAvailable() {
        return status == DriverStatus.AVAILABLE;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
