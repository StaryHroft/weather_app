package staryhroft.weatherapp.rest;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
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
        List<City> cities = cityService.getAllCitiesDesk();
        return ResponseEntity.ok(cities);
    }

    //Найти город по названию
    //GET /cities/{name}
    @GetMapping("/{name}")
    public ResponseEntity<Void> getCityByNameHeaders(@PathVariable String name) {
        return cityService.getCityByName(name);
    }

    //Добавить город в заголовок, запрос: curl -X POST http://localhost:8080/cities/add-head
    //                                         -H "name: Moscow" \
    //                                         -H "temp: 15.5"
    @PostMapping("/add")
    public ResponseEntity<Void> addCityWithHeaders(
            @RequestHeader("Name") String name,
            @RequestHeader("Temp") BigDecimal temp) {

        return cityService.addCity(name, temp);
    }
    //Добавить город в из БД в список любимых городов
    @PatchMapping("/{cityName}/favorite")
    public String setCityAsFavorite(@PathVariable String cityName) {
        return cityService.setCityAsFavorite(cityName);
    }

    @PatchMapping("/{cityName}/favorite/remove")
    public ResponseEntity<String> removeCityFromFavorites(@PathVariable String cityName) {
        String result = cityService.removeCityFromFavorites(cityName);
        return ResponseEntity.ok(result);
    }

    //Удалить город по названию
    //DELETE /cities/{name}
    @DeleteMapping("/del")
    public ResponseEntity<?> deleteCityByName(@RequestParam String name) {
        cityService.deleteCity(name);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    //Получить количество городов в базе
    //GET /cities/count
    @GetMapping("/count")
    public ResponseEntity<Long> countCities() {
        long count = cityService.countCities();
        return ResponseEntity.ok(count);
    }

    //Удалить все города из базы данных
    //DELETE /cities
    @DeleteMapping
    public ResponseEntity<Void> deleteAllCities() {
        cityService.deleteAllCities();
        return ResponseEntity.noContent().build();
    }
}