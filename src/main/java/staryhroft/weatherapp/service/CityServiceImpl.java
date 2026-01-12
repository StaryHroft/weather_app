package staryhroft.weatherapp.service;

import org.springframework.stereotype.Service;
import staryhroft.weatherapp.model.City;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CityServiceImpl implements CityService{
    private List<City> CITIES = Stream.of(
            new City(1L, 32.1F, "Bereza"),
            new City(2L, 28.5F, "Minsk"),
            new City(3L, 25.8F, "Moscow")
    ).collect(Collectors.toList());

    @Override
    public List<City> getAllCities() {
        return CITIES;
    }

    @Override
    public City getByCityName(String cityName) {
        return CITIES.stream().filter(city -> city.getCityName().equals(cityName))
                .findFirst()
                .orElse(new City("Город не найден"));
    }

    @Override
    public City create(City cityName) {
        this.CITIES.add(cityName);
        return cityName;
    }

    @Override
    public String deleteCity(String cityName) {
        boolean wasRemoved = this.CITIES.removeIf(x -> x.getCityName().equalsIgnoreCase(cityName));

        if (wasRemoved) {
            return "Город '" + cityName + "' успешно удален";
        } else {
            return "Город '" + cityName + "' не найден. Удаление не выполнено";
        }
    }


}