package ma.logitrack.fleet.domain;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import static ma.logitrack.fleet.domain.VehicleStatus.AVAILABLE;
import static ma.logitrack.fleet.domain.VehicleStatus.MAINTENANCE;
import static ma.logitrack.fleet.domain.VehicleStatus.ON_MISSION;
import static ma.logitrack.fleet.domain.VehicleStatus.OUT_OF_SERVICE;

public final class VehicleStatusPolicy {

    private static final Map<VehicleStatus, Set<VehicleStatus>> MANUAL_TRANSITIONS = Map.of(
            AVAILABLE, EnumSet.of(MAINTENANCE, OUT_OF_SERVICE),
            MAINTENANCE, EnumSet.of(AVAILABLE, OUT_OF_SERVICE),
            OUT_OF_SERVICE, EnumSet.of(MAINTENANCE),
            ON_MISSION, EnumSet.noneOf(VehicleStatus.class)
    );

    private VehicleStatusPolicy() {
    }

    public static boolean isManualTransitionAllowed(
            VehicleStatus from,
            VehicleStatus to
    ) {
        return MANUAL_TRANSITIONS
                .getOrDefault(from, Set.of())
                .contains(to);
    }
}
