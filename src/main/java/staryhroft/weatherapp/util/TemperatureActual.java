package staryhroft.weatherapp.util;

import staryhroft.weatherapp.model.City;

import java.time.LocalDateTime;
import java.time.Duration;

public class TemperatureActual {
    private static final long TEMPERATURE_VALID_HOURS = 24;

    public static boolean isTemperatureActual(City city) {
        if (city.getTemperatureUpdatedAt() == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        long hoursPassed = Duration.between(city.getTemperatureUpdatedAt(), now).toHours();

        return hoursPassed < TEMPERATURE_VALID_HOURS;
    }

    private TemperatureActual() {
    }
}