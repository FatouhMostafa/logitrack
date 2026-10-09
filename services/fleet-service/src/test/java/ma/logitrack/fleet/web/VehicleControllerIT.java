package ma.logitrack.fleet.web;

import java.util.UUID;

import ma.logitrack.fleet.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VehicleControllerIT extends IntegrationTestBase {

    @Test
    void shouldReturn201AndLocation_whenCreatingVehicle() throws Exception {
        mockMvc.perform(post(VEHICLES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson("90001-A-1")))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        containsString(VEHICLES_URL + "/")
                ))
                .andExpect(jsonPath("$.plate").value("90001-A-1"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void shouldReturn400WithFieldErrors_whenPlateBlank() throws Exception {
        mockMvc.perform(post(VEHICLES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson("")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.errors[*].field", hasItem("plate")));
    }

    @Test
    void shouldReturn409_whenPlateDuplicated() throws Exception {
        createAndReturnId(VEHICLES_URL, vehicleJson("90002-A-1"));

        mockMvc.perform(post(VEHICLES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson("90002-A-1")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Resource already exists"));
    }

    @Test
    void shouldReturn404ProblemDetail_whenVehicleUnknown() throws Exception {
        mockMvc.perform(get(
                        VEHICLES_URL + "/00000000-0000-0000-0000-000000000000"
                ))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnPage_withDefaultPagination() throws Exception {
        mockMvc.perform(get(VEHICLES_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldFilterByStatus() throws Exception {
        UUID id = createAndReturnId(
                VEHICLES_URL,
                vehicleJson("90003-A-1")
        );

        mockMvc.perform(patch(VEHICLES_URL + "/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"MAINTENANCE"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get(VEHICLES_URL)
                        .param("status", "MAINTENANCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.content[*].plate",
                        hasItem("90003-A-1")
                ))
                .andExpect(jsonPath(
                        "$.content[*].status",
                        everyItem(is("MAINTENANCE"))
                ));
    }

    @Test
    void shouldPairThenUnpairDriver() throws Exception {
        UUID vehicleId = createAndReturnId(
                VEHICLES_URL,
                vehicleJson("90004-A-1")
        );

        UUID driverId = createAndReturnId(
                DRIVERS_URL,
                driverJson("LIC-90004")
        );

        mockMvc.perform(put(
                        VEHICLES_URL + "/{id}/driver",
                        vehicleId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"driverId\":\"" + driverId + "\"}"
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentDriverId")
                        .value(driverId.toString()));

        mockMvc.perform(delete(
                        VEHICLES_URL + "/{id}/driver",
                        vehicleId
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentDriverId")
                        .value(nullValue()));
    }

    private static String vehicleJson(String plate) {
        return """
                {
                  "plate": "%s",
                  "model": "Renault Master",
                  "capacityKg": 1500
                }
                """.formatted(plate);
    }

    private static String driverJson(String licenseNumber) {
        return """
                {
                  "firstName": "Imane",
                  "lastName": "Tazi",
                  "phone": "+212610000099",
                  "licenseNumber": "%s"
                }
                """.formatted(licenseNumber);
    }
}
