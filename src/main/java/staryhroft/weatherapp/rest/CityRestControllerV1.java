package staryhroft.weatherapp.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import staryhroft.weatherapp.model.City;
import staryhroft.weatherapp.service.CityService;

import java.util.List;
@RestController
@RequestMapping("/cities")
public class CityRestControllerV1 {
//    private final CityService cityService;
//
//    public CityRestControllerV1(CityService cityService) {
//        this.cityService = cityService;
//    }
    // Создать таблицу
//    @PostMapping("/create")
//    public ResponseEntity<String> makeTable() {
//        cityService.createTable();
//        return ResponseEntity.ok("Таблица создана");
//    }
    // Показать все города из таблицы
    @GetMapping
    public String getCities() {
        List<City> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }
    //Показать город по названию
    @GetMapping("/{cityName}")
    public Object getCity(@PathVariable String cityName) {
        City city = cityService.getCityByName(cityName);

        if (city == null) {
            return "Город " + cityName + " не найден";
        }
        return city;
    }
    //Проверить наличие города в списке
    @GetMapping("/exists/{city}")
    public ResponseEntity<Boolean> checkCity(@PathVariable String cityName) {
        boolean exists = cityService.cityExists(cityName);
        return ResponseEntity.ok(exists);
    }

    @PostMapping(value = "/add", params = {"cityName", "temperature"})
    public ResponseEntity<String> addCity(@RequestParam String cityName,
                                          @RequestParam Float temperature) {
        cityService.addCity(cityName, temperature);
        return ResponseEntity.ok("Операция завершена");
    }
    @DeleteMapping("/api/cities/delete/{cityName}")
    public ResponseEntity<String> deleteCity(@PathVariable String cityName) {
        cityService.deleteCityByName(cityName);
        return ResponseEntity.ok("Город удален");
    }

//

//
////  /create?temperature=value&cityName=value
//    @GetMapping(value = "/create", params = {"temperature", "cityName"})
//    public List<City> createAndGetAllCities(@RequestParam String temperature, @RequestParam String cityName){
//        citiesService.create(temperature, cityName);
//        return citiesService.getAllCities();
//    }
//

}
//
//
//

//

//    }
//
//    @DeleteMapping("/delete/{city}")
//    public ResponseEntity<String> deleteCity(@PathVariable String city) {
//        cityService.deleteCity(city);
//        return ResponseEntity.ok("Город удален");
//    }
//}
