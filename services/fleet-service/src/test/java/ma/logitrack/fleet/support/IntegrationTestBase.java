package ma.logitrack.fleet.support;

import java.util.UUID;

import ma.logitrack.fleet.TestcontainersConfiguration;
import ma.logitrack.fleet.repository.DriverRepository;
import ma.logitrack.fleet.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class IntegrationTestBase {

    protected static final String DRIVERS_URL = "/api/v1/drivers";
    protected static final String VEHICLES_URL = "/api/v1/vehicles";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected VehicleRepository vehicles;

    @Autowired
    protected DriverRepository drivers;

    @BeforeEach
    void cleanDatabase() {
        vehicles.deleteAll();
        drivers.deleteAll();
    }

    protected String driverJson() {
        return """
                {
                  "firstName": "Youssef",
                  "lastName": "Amrani",
                  "phone": "+212612345678",
                  "licenseNumber": "LIC-IT-001"
                }
                """;
    }

    protected UUID createAndReturnId(String url, String json)
            throws Exception {
        String location = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        if (location == null || location.isBlank()) {
            throw new IllegalStateException(
                    "The response does not contain a Location header"
            );
        }

        return UUID.fromString(
                location.substring(location.lastIndexOf('/') + 1)
        );
    }
}
