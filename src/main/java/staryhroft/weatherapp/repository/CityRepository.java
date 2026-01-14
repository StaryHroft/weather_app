package staryhroft.weatherapp.repository;

import staryhroft.weatherapp.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository {

// Создать таблицу
    void createTable();
// Показать все города из таблицы
    List<City> getAllCities();
// Показать город по названию
    City getCityByName(String cityName);
// Проверить есть ли город в таблице
    boolean existsByCity(String cityName);
// Добавить в таблицу город
    void addCityToTable(City city);
// Удалить город из таблицы
    void deleteCityByName(String cityName);
}