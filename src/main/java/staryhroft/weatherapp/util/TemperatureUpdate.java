package staryhroft.weatherapp.util;

import staryhroft.weatherapp.model.City;
import staryhroft.weatherapp.service.WeatherApiResponse;

import java.time.LocalDateTime;

public class TemperatureUpdate {
    public static void updateCityTemperature(City city, WeatherApiResponse apiResponse) {
        city.setTemperature(apiResponse.getTemperature());
        city.setTemperatureUpdatedAt(LocalDateTime.now());
        city.setUpdatedAt(LocalDateTime.now());
    }

    private TemperatureUpdate() {
    }
}
