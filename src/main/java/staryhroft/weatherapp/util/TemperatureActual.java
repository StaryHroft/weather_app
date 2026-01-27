package staryhroft.weatherapp.util;

import staryhroft.weatherapp.entity.City;

import java.time.LocalDateTime;
import java.time.Duration;

public class TemperatureActual {
    private static final long TEMPERATURE_VALID_HOURS = 24;

    public static boolean isTemperatureActual(City city) {
        if (city.getTemperatureUpdatedAt() == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime updatedAt = city.getTemperatureUpdatedAt();

        Duration duration = Duration.between(updatedAt, now);
        // Используйте toMinutes() для большей точности
        return duration.toMinutes() < (TEMPERATURE_VALID_HOURS * 60L);
    }

    private TemperatureActual() {
    }
}