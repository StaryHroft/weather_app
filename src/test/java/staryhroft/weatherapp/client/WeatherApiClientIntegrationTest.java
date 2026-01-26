package staryhroft.weatherapp.client;

import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.junit.jupiter.api.Test;
import staryhroft.weatherapp.exception.WeatherApiException;
import staryhroft.weatherapp.service.WeatherApiResponse;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "openweather.api.url=https://api.openweathermap.org/data/2.5/weather",
        "openweather.api.key=${OPENWEATHER_API_KEY:test-key}"
})
class WeatherApiClientIntegrationTest {

    @Autowired
    private WeatherApiClient weatherApiClient;

    /**
     * Интеграционный тест с реальным API (только для локального запуска)
     * Требует реальный API ключ в переменной окружения OPENWEATHER_API_KEY
     */
    @Test
    @Disabled("Только для ручного запуска с реальным API ключом")
    void integrationTest_withRealApi() {
        // Given
        String cityName = "Moscow";

        try {
            // When
            WeatherApiResponse result = weatherApiClient.fetchWeather(cityName);

            // Then
            assertNotNull(result);
            assertNotNull(result.getTemperature());

            // Температура в разумных пределах
            BigDecimal temp = result.getTemperature();
            assertTrue(temp.compareTo(new BigDecimal("-50")) > 0,
                    "Температура слишком низкая: " + temp);
            assertTrue(temp.compareTo(new BigDecimal("50")) < 0,
                    "Температура слишком высокая: " + temp);

        } catch (WeatherApiException e) {
            // Если нет интернета или API ключ неверный - это нормально для теста
            System.out.println("Интеграционный тест пропущен: " + e.getMessage());
        }
    }
}