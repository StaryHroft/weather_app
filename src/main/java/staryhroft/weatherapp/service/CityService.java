package staryhroft.weatherapp.service;

import staryhroft.weatherapp.model.City;

import java.math.BigDecimal;
import java.util.List;

public interface CityService {

    //Получить все города из БД
    List<City> getAllCities();

    // Найти город по названию
    City getCityByName(String name);

    //Добавить новый город
    City addCity(City city);

    //Удалить город по названию
    void deleteCity(String name);

    //Проверить, существует ли город с указанным названием
    boolean existsByName(String name);

    //Получить общее количество городов в базе
    long countCities();

    //Получить город с максимальной температурой
    City getWarmestCity();

    //Получить город с минимальной температурой
    City getColdestCity();

    //Получить среднюю температуру по всем городам
    BigDecimal getAverageTemperature();

    //Удалить все города из базы данных
    void deleteAllCities();
}
