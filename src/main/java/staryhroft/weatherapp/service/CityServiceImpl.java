package staryhroft.weatherapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staryhroft.weatherapp.client.WeatherApiClient;
import staryhroft.weatherapp.dto.response.CityDto;
import staryhroft.weatherapp.dto.response.FavoriteDto;
import staryhroft.weatherapp.exception.CityNotFoundException;
import staryhroft.weatherapp.mapper.CityMapper;
import staryhroft.weatherapp.model.City;
import staryhroft.weatherapp.repository.CityRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static staryhroft.weatherapp.util.CreateCityFromApi.createCityFromApiResponse;
import static staryhroft.weatherapp.util.TemperatureActual.isTemperatureActual;
import static staryhroft.weatherapp.util.TemperatureUpdate.updateCityTemperature;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CityServiceImpl implements CityService {
    static final int MAX_FAVORITES = 3;


    private final CityRepository cityRepository;

    private final WeatherApiClient weatherApiClient;
    private final CityMapper cityMapper;

    private final ObjectMapper objectMapper;

    //Получить все города из БД
    @Override
    @Transactional(readOnly = true)
    public List<CityDto> getAllCitiesSorted() {
        List<City> cities = cityRepository.findAllWithFavoritesFirst();

        return cities.stream()
                .map(this::convertToDto)
                .toList();
    }

    private CityDto convertToDto(City city) {
        CityDto cityDto = new CityDto();
        cityDto.setId(city.getId());
        cityDto.setName(city.getName());
        cityDto.setTemperature(city.getTemperature());
        cityDto.setFavorite(city.getFavorite());
        cityDto.setTemperatureUpdatedAt(city.getTemperatureUpdatedAt());

        return cityDto;
    }

    //Получить информацию о городе
    @Override
    @Transactional
    public CityDto getWeatherByCityName(String cityName) {
        City city = cityRepository.findByName(cityName);
        if (city != null && isTemperatureActual(city)) {
            return cityMapper.toDto(city);
        } else {
            WeatherApiResponse apiResponse = weatherApiClient.fetchWeather(cityName);

            if (city == null) {
                city = createCityFromApiResponse(cityName, apiResponse);
                cityRepository.save(city);
            } else {
                updateCityTemperature(city, apiResponse);
                cityRepository.save(city);
            }
            return cityMapper.toDto(city);
            }
        }


    //Передать один из имеющихся городов в любимые города
    @Override
    @Transactional
    public FavoriteDto setCityAsFavorite(String cityName) {
        City city = cityRepository.findByName(cityName);

        if (city == null) {
            throw new CityNotFoundException("Город '" + cityName + "' не найден");
        }

        if (city.getFavorite()) {
            return FavoriteDto.error("Город уже в избранном");
        }
        long favoriteCount = cityRepository.countByFavoriteTrue();
        if (favoriteCount >= MAX_FAVORITES) {
            return FavoriteDto.error(
                    String.format("Нельзя добавить более %d городов в избранное. " +
                            "Текущее количество: %d. Удалите город из избранного", MAX_FAVORITES, favoriteCount)
            );
        }
        city.setFavorite(true);
        city.setUpdatedAt(LocalDateTime.now());
        cityRepository.save(city);

        return FavoriteDto.added(city.getId(), city.getName());
    }

    // Удалить город из списка любимых городов
    @Override
    @Transactional
    public FavoriteDto removeCityFromFavorites(String cityName) {
        City city = cityRepository.findByName(cityName);

        if (city == null) {
            throw new CityNotFoundException(cityName);
        }

        if (!city.getFavorite()) {
            return FavoriteDto.error("Город отсутствует в списке любимых городов");
        }

        city.setFavorite(false);
        city.setUpdatedAt(LocalDateTime.now());
        cityRepository.save(city);

        return FavoriteDto.removed(city.getId(), city.getName());
    }

    //Удалить город по названию

    @Override
    @Transactional
    public void deleteCity(String name) {
        if (!cityRepository.existsByName(name)) {
            throw new CityNotFoundException(name);
        }
        cityRepository.deleteByName(name);
    }

    //Получить количество городов в базе
    @Override
    @Transactional(readOnly = true)
    public long countCities() {
        return cityRepository.count();
    }

    //Удалить все города из базы данных
    @Override
    @Transactional
    public void deleteAllCities() {
        cityRepository.deleteAll();
    }
}