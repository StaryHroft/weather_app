package staryhroft.weatherapp.util;

import staryhroft.weatherapp.entity.City;
import staryhroft.weatherapp.service.WeatherApiResponse;

import java.time.LocalDateTime;

public class TemperatureUpdate {
    public static void updateCityTemperature(City city, WeatherApiResponse apiResponse) {
        city.setTemperature(apiResponse.getTemperature());
        city.setUpdatedAt(LocalDateTime.now());
    }

    private TemperatureUpdate() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
