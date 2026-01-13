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
//  /create?temperature=value&cityName=value
    @GetMapping(value = "/create", params = {"temperature", "cityName"})
    public List<City> createAndGetAllCities(@RequestParam String temperature, @RequestParam String cityName){
        citiesService.create(temperature, cityName);
        return citiesService.getAllCities();
    }
    @GetMapping("/delete/{cityName}")
    public String deleteByCityName(@PathVariable String cityName) {
        return citiesService.deleteCity(cityName);
    }
}
