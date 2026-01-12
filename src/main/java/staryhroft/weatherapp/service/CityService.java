package staryhroft.weatherapp.service;

import staryhroft.weatherapp.model.City;

import java.util.List;

public interface CityService {
    List<City> getAllCities();
    City getByCityName(String cityName);
    City create(City cityName);
    String deleteCity(String cityName);
}
