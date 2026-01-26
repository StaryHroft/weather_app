package staryhroft.weatherapp.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import staryhroft.weatherapp.dto.response.CityDto;
import staryhroft.weatherapp.dto.response.FavoriteDto;
import staryhroft.weatherapp.service.CityService;

import java.util.List;

@Slf4j
@ComponentScan
@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityRestControllerV1 {

    private final CityService cityService;

    //Получить все города из БД
    //GET /cities
    @GetMapping
    public ResponseEntity<List<CityDto>> getAllCitiesRequest() {
        return ResponseEntity.ok(cityService.getAllCitiesSorted());
    }

    //Найти город по названию
    //GET /cities/{name}
    @GetMapping("/{name}")
    public ResponseEntity<CityDto> getCityByNameRequest(@PathVariable String name) {
        CityDto cityDto = cityService.getWeatherByCityName(name);
        return ResponseEntity.ok(cityDto);
    }

    //Добавить город в из БД в список любимых городов PATCH /cities/{cityName}/favorite
    @PatchMapping("/{cityName}/favorite")
    public ResponseEntity<FavoriteDto> setCityAsFavoriteRequest(@PathVariable String cityName) {
        return ResponseEntity.ok(cityService.setCityAsFavorite(cityName));
    }


//Убрать город в из списка любимых городов PATCH /cities/{cityName}/favorite/remove
    @PatchMapping("/{cityName}/favorite/remove")
    public ResponseEntity<FavoriteDto> removeCityFromFavoritesRequest(@PathVariable String cityName) {
        return ResponseEntity.ok(cityService.removeCityFromFavorites(cityName));
    }

    //Удалить город по названию DELETE /cities/{name}
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteCityRequest(@PathVariable String name) {
        log.info("Удаление города: {}", name);
        cityService.deleteCity(name);
        return ResponseEntity.noContent().build();
    }

    //Получить количество городов в базе GET /cities/count
    @GetMapping("/count")
    public ResponseEntity<Long> countCitiesRequest() {
        return ResponseEntity.ok(cityService.countCities());
    }

    //Удалить все города из базы данных DELETE /cities
    @DeleteMapping
    public ResponseEntity<Void> deleteAllCitiesRequest() {
        cityService.deleteAllCities();
        return ResponseEntity.noContent().build();
    }
}