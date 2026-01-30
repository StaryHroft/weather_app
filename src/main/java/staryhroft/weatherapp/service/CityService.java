package staryhroft.weatherapp.service;

import staryhroft.weatherapp.dto.response.CityDto;
import staryhroft.weatherapp.dto.response.FavoriteDto;

import java.util.List;

public interface CityService {

    //Получить все города из БД
    List<CityDto> getAllCitiesSorted();

    //Получить информацию о городе
    CityDto getByCityName(String name);

    //Добавить город из списка в Любимые города
    FavoriteDto setCityAsFavorite(String cityName);

    // Удалить город из списка любимых городов
    FavoriteDto removeCityFromFavorites(String cityName);

    //Удалить город по названию
    void deleteCity(String name);

    //Получить общее количество городов в базе
    long countCities();

    //Удалить все города из базы данных
    void deleteAllCities();
}
