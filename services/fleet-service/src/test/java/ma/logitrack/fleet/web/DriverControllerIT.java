package ma.logitrack.fleet.web;

import ma.logitrack.fleet.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DriverControllerIT extends IntegrationTestBase {

    @Test
    void shouldReturn201AndLocation_whenCreatingDriver() throws Exception {
        mockMvc.perform(post(DRIVERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("Youssef"))
                .andExpect(jsonPath("$.lastName").value("Amrani"));
    }

    @Test
    void shouldReturn409_whenLicenseNumberDuplicated() throws Exception {
        mockMvc.perform(post(DRIVERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson()))
                .andExpect(status().isCreated());

        mockMvc.perform(post(DRIVERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Salma",
                                  "lastName": "Idrissi",
                                  "phone": "+212612345679",
                                  "licenseNumber": "LIC-IT-001"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldChangeAvailability_whenDriverAvailable() throws Exception {
        UUID driverId = createAndReturnId(DRIVERS_URL, driverJson());

        mockMvc.perform(patch(
                        DRIVERS_URL + "/{id}/availability", driverId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"OFF_DUTY"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(driverId.toString()))
                .andExpect(jsonPath("$.status").value("OFF_DUTY"));
    }
}
