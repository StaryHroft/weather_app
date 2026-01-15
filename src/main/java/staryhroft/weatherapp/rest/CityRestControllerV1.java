package staryhroft.weatherapp.rest;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import staryhroft.weatherapp.model.City;
import staryhroft.weatherapp.service.CityService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@ComponentScan
@RestController
@RequestMapping("/cities")
public class CityRestControllerV1 {

    private final CityService cityService;

    @Autowired
    public CityRestControllerV1(CityService cityService) {
        this.cityService = cityService;
    }

    //Получить все города из БД
    //GET /cities
    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        List<City> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }


    //Найти город по названию
    //GET /cities/{name}

    @GetMapping("/{name}")
    public ResponseEntity<City> getCityByName(@PathVariable String name) {
            City city = cityService.getCityByName(name);
            return ResponseEntity.ok(city);
    }

    //Добавить город через параметры запроса
    // POST /cities/add?name=Moscow&temp=15.5
    @PostMapping("/add")
    public ResponseEntity<?> addCityWithParams(
            @RequestParam String name,
            @RequestParam BigDecimal temp) {
        log.info("POST /cities/add вызван с параметрами: name={}, temp={}", name, temp);

        City city = new City();
        city.setName(name);
        city.setTemperature(temp);

        try {
            City createdCity = cityService.addCity(city);
            log.info("Город создан: ID={}, name={}", createdCity.getId(), createdCity.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
        } catch (RuntimeException e) {
            log.error("Ошибка при создании города: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Удалить город по названию
     * DELETE /cities/{name}
     */
    @DeleteMapping("/del")
    public ResponseEntity<?> deleteCity(@RequestParam String name) {
        try {
            cityService.deleteCity(name);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    /**
     * Проверить существование города
     * GET /cities/exists/{name}
     */
    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        boolean exists = cityService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    /**
     * Получить количество городов
     * GET /cities/count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCities() {
        long count = cityService.countCities();
        return ResponseEntity.ok(count);
    }

    /**
     * Получить самый теплый город
     * GET /cities/warmest
     */
    @GetMapping("/warmest")
    public ResponseEntity<City> getWarmestCity() {
        try {
            City city = cityService.getWarmestCity();
            return ResponseEntity.ok(city);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build(); // 204 если нет городов
        }
    }

    /**
     * Получить самый холодный город
     * GET /cities/coldest
     */
    @GetMapping("/coldest")
    public ResponseEntity<City> getColdestCity() {
        try {
            City city = cityService.getColdestCity();
            return ResponseEntity.ok(city);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build(); // 204 если нет городов
        }
    }

    /**
     * Получить среднюю температуру
     * GET /cities/average-temperature
     */
    @GetMapping("/average-temperature")
    public ResponseEntity<BigDecimal> getAverageTemperature() {
        try {
            BigDecimal average = cityService.getAverageTemperature();
            return ResponseEntity.ok(average);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build(); // 204 если нет городов
        }
    }

    /**
     * Удалить все города
     * DELETE /cities
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllCities() {
        try {
            cityService.deleteAllCities();
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


//@RestController
//@RequestMapping("/cities")
//public class CityRestControllerV1 {
//    private final CityRepositoryImpl cityRepository;
//
//    @Autowired
//    public CityRestControllerV1(CityRepositoryImpl cityRepository) {
//        this.cityRepository = cityRepository;
//    }
//     //Создать таблицу
//    @PostMapping("/create")
//    public ResponseEntity<String> makeTable() {
//        cityRepository.createTable();
//        return ResponseEntity.ok("Таблица создана");
//    }
//
//    // Показать все города из таблицы
//    @GetMapping
//    public ResponseEntity<List<City>> getCities() {
//        return ResponseEntity.ok(cityRepository.getCities());
//    }
//
//    //Показать город по названию
//    @GetMapping("/{name}")
//    public ResponseEntity<City> getCity(@PathVariable String name) {
//        City city = cityRepository.getCity(name);
//        if (city == null) {
//            return ResponseEntity.notFound().build(); // 404
//        }
//        return ResponseEntity.ok(city); // 200 OK с телом
//    }
//
//    //Добавить город
//    @PostMapping("/add")
//    public ResponseEntity<?> addCity(@RequestParam String name,
//                                     @RequestParam Float temp) {
//        City city = new City();
//        city.setName(name);
//        city.setTemp(temp);
//
//        cityRepository.addCity(city);
//        return ResponseEntity.ok("Город '" + name + "' успешно добавлен");
//    }
//
//    //Удалить город
//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteCity(@RequestParam String name) {
//        cityRepository.deleteCity(name);
//        return ResponseEntity.ok("Город удален");
//    }
//}

//    //Проверить наличие города в списке
//    @GetMapping("/exists/{city}")
//    public ResponseEntity<Boolean> checkCity(@PathVariable String cityName) {
//        boolean exists = cityService.cityExists(cityName);
//        return ResponseEntity.ok(exists);
//    }

//

//    }
//
//    @DeleteMapping("/delete/{city}")
//    public ResponseEntity<String> deleteCity(@PathVariable String city) {
//        cityService.deleteCity(city);
//        return ResponseEntity.ok("Город удален");
//    }
//}
