package staryhroft.weatherapp.rest;

import org.springframework.web.bind.annotation.*;
import staryhroft.weatherapp.model.City;
import staryhroft.weatherapp.service.CityService;

import java.util.List;
@RestController
@RequestMapping("/api/v1/cities")
public class CityRestControllerV1 {
    private final CityService citiesService;

    public CityRestControllerV1(CityService citiesService) {
        this.citiesService = citiesService;
    }

    @GetMapping
    public List<City> getAll(){
        return citiesService.getAllCities();
    }
    @GetMapping("/{cityName}")
    public City getByCityName(@PathVariable String cityName){
        return citiesService.getByCityName(cityName);
    }
    @GetMapping("/create/{cityName}")
    public List<City> createAndGetAllCities(@PathVariable String cityName, Float temperature ){
        citiesService.create(cityName, temperature);
        return citiesService.getAllCities();
    }
    @GetMapping("/delete/{cityName}")
    public String deleteByCityName(@PathVariable String cityName) {
        return citiesService.deleteCity(cityName);
    }
}
