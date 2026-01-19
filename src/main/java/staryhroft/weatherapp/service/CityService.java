package staryhroft.weatherapp.service;

import org.springframework.http.ResponseEntity;
import staryhroft.weatherapp.model.City;

import java.math.BigDecimal;
import java.util.List;

public interface CityService {

    //Получить все города из БД
    List<City> getAllCitiesDesk();

    // Найти город по названию
    ResponseEntity<Void> getCityByName(String name);

    //Добавить новый город
    ResponseEntity<Void> addCity(String name, BigDecimal temp);

    //Добавить город из списка в Любимые города
    String setCityAsFavorite(String cityName);

    // Удалить город из списка любимых городов
    String removeCityFromFavorites(String cityName);

    //Удалить город по названию
    void deleteCity(String name);

    //Получить общее количество городов в базе
    long countCities();

    //Удалить все города из базы данных
    void deleteAllCities();
}
