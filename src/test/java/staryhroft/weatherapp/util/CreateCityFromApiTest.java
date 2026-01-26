package staryhroft.weatherapp.util;

import org.junit.jupiter.api.Test;
import staryhroft.weatherapp.model.City;
import staryhroft.weatherapp.service.WeatherApiResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreateCityFromApiTest {

    @Test
    void createCityFromApiResponse_ShouldCreateCityWithCorrectData() {
        // Arrange
        String cityName = "Moscow";
        BigDecimal temperature = new BigDecimal("25.5");
        WeatherApiResponse apiResponse = new WeatherApiResponse(temperature);

        // Act
        City result = CreateCityFromApi.createCityFromApiResponse(cityName, apiResponse);

        // Assert
        assertNotNull(result);
        assertEquals(cityName, result.getName());
        assertEquals(temperature, result.getTemperature());
        assertNotNull(result.getTemperatureUpdatedAt());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        // All timestamps should be very close to each other
        assertEquals(result.getTemperatureUpdatedAt(), result.getCreatedAt());
        assertEquals(result.getCreatedAt(), result.getUpdatedAt());
    }

    @Test
    void createCityFromApiResponse_ShouldThrowNPE_WhenCityNameIsNull() {
        // Arrange
        WeatherApiResponse apiResponse = new WeatherApiResponse(new BigDecimal("20.0"));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            CreateCityFromApi.createCityFromApiResponse(null, apiResponse);
        });
    }

    @Test
    void createCityFromApiResponse_ShouldThrowNPE_WhenApiResponseIsNull() {
        // Arrange
        String cityName = "Berlin";

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            CreateCityFromApi.createCityFromApiResponse(cityName, null);
        });
    }

    @Test
    void createCityFromApiResponse_ShouldHandleEdgeCases() {
        // Test with empty city name
        City result1 = CreateCityFromApi.createCityFromApiResponse("",
                new WeatherApiResponse(new BigDecimal("10.0")));
        assertEquals("", result1.getName());

        // Test with null temperature
        City result2 = CreateCityFromApi.createCityFromApiResponse("London",
                new WeatherApiResponse(null));
        assertNull(result2.getTemperature());

        // Test with negative temperature
        City result3 = CreateCityFromApi.createCityFromApiResponse("Yakutsk",
                new WeatherApiResponse(new BigDecimal("-40.0")));
        assertEquals(new BigDecimal("-40.0"), result3.getTemperature());
    }
}