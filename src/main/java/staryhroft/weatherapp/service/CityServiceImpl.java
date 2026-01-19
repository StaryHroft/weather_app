package staryhroft.weatherapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import staryhroft.weatherapp.model.City;
import staryhroft.weatherapp.repository.CityRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CityServiceImpl implements CityService {
    private static final int MAX_FAVORITES = 3;
    private final CityRepository cityRepository;

    //Получить все города из БД
    @Override
    @Transactional(readOnly = true)
    public List<City> getAllCitiesDesk() {
        return cityRepository.findAllWithFavoritesFirst();
    }


    // Найти город по названию
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Void> getCityByName(String name) {
        City city = cityRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Город '" + name + "' не найден"
                ));

        HttpHeaders head = new HttpHeaders();
        head.add("X-City-Name", city.getName());
        head.add("X-City-Temperature", city.getTemperature().toString());

        return ResponseEntity.ok()
                .headers(head)
                .build();
    }


    //Добавить город
    @Override
    @Transactional
    public ResponseEntity<Void> addCity(String name, BigDecimal temperature) {
        if (cityRepository.existsByName(name)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Город с именем '" + name + "' уже существует. Добавление отменено"
            );
        }
        City city = new City();
        city.setName(name);
        city.setTemperature(temperature);

        City createdCity = cityRepository.save(city);
        HttpHeaders head = new HttpHeaders();
        head.add("X-Created-City-Name", createdCity.getName());
        head.add("X-Created-City-Temp", createdCity.getTemperature().toString());
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(head)
                .build();
    }

    // Передать один из имеющихся городов в любимые города
    @Override
    @Transactional
    public String setCityAsFavorite(String cityName) {
        City city = cityRepository.findByName(cityName)
                .orElseThrow(() -> new RuntimeException("Город '" + cityName + "' не найден"));
        // Если город уже в любимых
        if (city.getFavorite()) {
            return "Город " + cityName + " уже является любимым городом";
        }
        // Если есть свободная квота
        if (cityRepository.countFavoriteCities() < MAX_FAVORITES) {
            city.setFavorite(true);
            cityRepository.save(city);
            return "Город " + cityName + " добавлен в список любимых городов";
        }

        // Если нет свободных квот
        return "Достигнут лимит любимых городов. Выберите город для замены.";
    }
    // Удалить город из списка любимых городов
    @Override
    @Transactional
    public String removeCityFromFavorites(String cityName) {
        City city = cityRepository.findByName(cityName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Город '" + cityName + "' не найден"
                ));

        city.setFavorite(false);
        cityRepository.save(city);

        return "Город " + cityName + " убран из списка любимых городов";
    }

    //Удалить город по названию
    @Override
    @Transactional
    public void deleteCity(String name) {
        City city = cityRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Город '" + name + "' не найден"
                ));

        cityRepository.delete(city);
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
        try {
            cityRepository.deleteAll();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при удалении всех городов: " + e.getMessage()
            );
        }
    }
}