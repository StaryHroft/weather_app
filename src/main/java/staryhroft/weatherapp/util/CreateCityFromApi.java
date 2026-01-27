package staryhroft.weatherapp.util;

import staryhroft.weatherapp.entity.City;
import staryhroft.weatherapp.service.WeatherApiResponse;

import java.time.LocalDateTime;

public class CreateCityFromApi {
    public static City createCityFromApiResponse(String cityName, WeatherApiResponse apiResponse) {
        City city = new City();
        city.setName(cityName);
        city.setTemperature(apiResponse.getTemperature());
        city.setTemperatureUpdatedAt(LocalDateTime.now());
        city.setCreatedAt(LocalDateTime.now());
        city.setUpdatedAt(LocalDateTime.now());
        return city;
    }

    private CreateCityFromApi() {
    }
}
