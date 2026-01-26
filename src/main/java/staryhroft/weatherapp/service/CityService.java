package staryhroft.weatherapp.service;

import staryhroft.weatherapp.dto.request.CityCreateDto;
import staryhroft.weatherapp.dto.response.CityDto;
import staryhroft.weatherapp.dto.response.FavoriteDto;
import staryhroft.weatherapp.model.City;

import java.util.List;
import java.util.Optional;

public interface CityService {

    //Получить все города из БД
    List<CityDto> getAllCitiesSorted();

    //Получить информацию о городе
    CityDto getWeatherByCityName(String name);

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
