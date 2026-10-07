package ma.logitrack.fleet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 15)
    private String plate;

    @Column(nullable = false, length = 60)
    private String model;

    @Column(name = "capacity_kg", nullable = false)
    private int capacityKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status;

    @Column(name = "current_driver_id")
    private UUID currentDriverId;

    @Column(name = "current_delivery_id")
    private UUID currentDeliveryId;

    @Version
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Vehicle create(String plate, String model, int capacityKg) {
        Vehicle vehicle = new Vehicle();
        vehicle.plate = plate;
        vehicle.model = model;
        vehicle.capacityKg = capacityKg;
        vehicle.status = VehicleStatus.AVAILABLE;
        return vehicle;
    }

    public void updateDetails(String model, int capacityKg) {
        this.model = model;
        this.capacityKg = capacityKg;
    }

    public void changeStatus(VehicleStatus newStatus) {
        this.status = newStatus;
    }

    public void pairWith(UUID driverId) {
        this.currentDriverId = driverId;
    }

    public void unpair() {
        this.currentDriverId = null;
    }

    public boolean isOnMission() {
        return status == VehicleStatus.ON_MISSION;
    }

    public boolean hasDriver() {
        return currentDriverId != null;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
