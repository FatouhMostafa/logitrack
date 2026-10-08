package ma.logitrack.fleet.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleStatusPolicyTest {

    @ParameterizedTest(name = "{0} → {1} Allowed")
    @CsvSource({
            "AVAILABLE, MAINTENANCE",
            "AVAILABLE, OUT_OF_SERVICE",
            "MAINTENANCE, AVAILABLE",
            "MAINTENANCE, OUT_OF_SERVICE",
            "OUT_OF_SERVICE, MAINTENANCE"
    })
    void shouldAllow_documentedManualTransitions(VehicleStatus from, VehicleStatus to) {
        assertThat(VehicleStatusPolicy.isManualTransitionAllowed(from, to)).isTrue();
    }

    @ParameterizedTest(name = "{0} → ON_MISSION rejected")
    @EnumSource(VehicleStatus.class)
    void shouldReject_anyManualTransitionToOnMission(VehicleStatus from) {
        assertThat(VehicleStatusPolicy.isManualTransitionAllowed(from, VehicleStatus.ON_MISSION)).isFalse();
    }

    @Test
    void shouldReject_outOfServiceToAvailable() {
        assertThat(VehicleStatusPolicy.isManualTransitionAllowed(VehicleStatus.OUT_OF_SERVICE, VehicleStatus.AVAILABLE)).isFalse();
    }

    @Test
    void shouldReject_anyManualTransitionFromOnMission() {
        assertThat(VehicleStatusPolicy.isManualTransitionAllowed(VehicleStatus.ON_MISSION, VehicleStatus.AVAILABLE)).isFalse();
    }
}
