package staryhroft.weatherapp.service;

import staryhroft.weatherapp.model.City;

import java.util.List;

public interface CityService {
    List<City> getAllCities();
    City getByCityName(String cityName);
    void create(String cityName, Float temperature);
    String deleteCity(String cityName);
}
