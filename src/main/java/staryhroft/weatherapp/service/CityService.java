package staryhroft.weatherapp.service;

import staryhroft.weatherapp.model.City;

import java.util.List;

public interface CityService {
    // Создать таблицу
    void createTable();
    // Показать все города из таблицы
    List<City> getAllCities();
    //Показать город по названию
    City getCityByName(String cityName);
    //Проверить наличие города в списке
    boolean cityExists(String cityName);
    // Добавление города
    void addCity(String cityName, Float temperature);
    //Удаление города
    void deleteCityByName(String cityName);

    //void addCityIfNotExists(String cityName, Float temperature);
}
