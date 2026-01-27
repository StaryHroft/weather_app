package staryhroft.weatherapp.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import staryhroft.weatherapp.entity.City;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemperatureActualTest {

    // Mock для City
    private City createMockCity(LocalDateTime temperatureUpdatedAt) {
        City city = mock(City.class);

        when(city.getId()).thenReturn(1L);
        when(city.getName()).thenReturn("TestCity");
        when(city.getTemperatureUpdatedAt()).thenReturn(temperatureUpdatedAt);
        return city;
    }

    @Test
    void isTemperatureActual_ShouldReturnFalse_WhenTemperatureUpdatedAtIsNull() {
        // Given
        City city = createMockCity(null);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertFalse(result);
    }

    @Test
    void isTemperatureActual_ShouldReturnTrue_WhenTemperatureUpdatedLessThan24HoursAgo() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyThreeHoursAgo = now.minusHours(23);
        City city = createMockCity(twentyThreeHoursAgo);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertTrue(result);
    }

    @Test
    void isTemperatureActual_ShouldReturnFalse_WhenTemperatureUpdatedExactly24HoursAgo() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);
        City city = createMockCity(twentyFourHoursAgo);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertFalse(result, "Должно вернуть false, если прошло ровно 24 часа");
    }

    @Test
    void isTemperatureActual_ShouldReturnFalse_WhenTemperatureUpdatedMoreThan24HoursAgo() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFiveHoursAgo = now.minusHours(25);
        City city = createMockCity(twentyFiveHoursAgo);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertFalse(result);
    }

    @Test
    void isTemperatureActual_ShouldReturnTrue_WhenTemperatureUpdatedJustNow() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        City city = createMockCity(now);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertTrue(result);
    }

    @Test
    void isTemperatureActual_ShouldReturnTrue_WhenTemperatureUpdated23Hours59MinutesAgo() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime almost24HoursAgo = now.minusHours(23).minusMinutes(59);
        City city = createMockCity(almost24HoursAgo);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertTrue(result, "Должно вернуть true, если прошло 23 часа 59 минут");
    }

    @Test
    void isTemperatureActual_ShouldReturnFalse_WhenTemperatureUpdated24Hours1MinuteAgo() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime justOver24HoursAgo = now.minusHours(24).minusMinutes(1);
        City city = createMockCity(justOver24HoursAgo);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertFalse(result, "Должно вернуть false, если прошло 24 часа 1 минута");
    }

    @Test
    void isTemperatureActual_ShouldHandleFutureDate_ReturnTrue() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusHours(1); // Будущая дата (может быть из-за рассинхронизации времени)
        City city = createMockCity(futureDate);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertTrue(result, "Должно вернуть true для будущей даты (отрицательная duration)");
    }

    @Test
    void isTemperatureActual_ShouldReturnFalse_WhenTemperatureUpdatedAtIsFarInPast() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime farPast = now.minusDays(30); // 30 дней назад
        City city = createMockCity(farPast);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertFalse(result);
    }

    @Test
    void isTemperatureActual_ShouldWorkWithRealCityObject() {
        // Интеграционный тест с реальным объектом City
        // Given
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(12);
        City city = new City();
        city.setTemperatureUpdatedAt(updatedAt);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertTrue(result);
    }

    @Test
    void isTemperatureActual_ShouldHandleEdgeCaseOf23Hours59Minutes59Seconds() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime edgeCaseTime = now
                .minusHours(23)
                .minusMinutes(59)
                .minusSeconds(59);

        City city = createMockCity(edgeCaseTime);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertTrue(result, "Должно вернуть true для 23:59:59");
    }

    @Test
    void isTemperatureActual_ShouldHandleEdgeCaseOfExactly24Hours() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime exactly24HoursAgo = now
                .minusHours(24)
                .minusSeconds(0);

        City city = createMockCity(exactly24HoursAgo);

        // When
        boolean result = TemperatureActual.isTemperatureActual(city);

        // Then
        assertFalse(result, "Должно вернуть false для ровно 24 часов");
    }
}
