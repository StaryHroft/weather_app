package staryhroft.weatherapp.util;

import staryhroft.weatherapp.entity.City;

import java.time.LocalDateTime;
import java.time.Duration;

public class TemperatureActual {
    private static final long TEMPERATURE_VALID_HOURS = 24;

    public static boolean isTemperatureActual(City city) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime updatedAt = city.getUpdatedAt();

            // Если время обновления не установлено
            if (updatedAt == null) {
                return false;
            }
            Duration duration = Duration.between(updatedAt, now);
            return duration.toHours() < TEMPERATURE_VALID_HOURS;
        }

    private TemperatureActual() {
    }
}