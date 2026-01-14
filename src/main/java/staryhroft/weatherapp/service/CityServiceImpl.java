package staryhroft.weatherapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import staryhroft.weatherapp.model.City;
import staryhroft.weatherapp.repository.CityRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CityServiceImpl implements CityService{
    @Autowired
    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }
    // Создать таблицу
    @Override
    public void createTable() {
        cityRepository.createTable();
    }
    // Показать все города из таблицы
    @Override
    public List<City> getAllCities() {
        return cityRepository.getAllCities();
    }
    //Показать город по названию
    @Override
    public City getCityByName(String cityName) {
        City city = cityRepository.getCityByName(cityName);
        if (city == null) {
            System.out.println("Город '" + cityName + "' не найден");
        }
        return city;
    }
    //Проверить наличие города в списке
    @Override
    public boolean cityExists(String cityName) {
        return cityRepository.existsByCity(cityName);
    }
    // Добавление города
    @Override
    public void addCity(String cityName, Float temperature) {
        City city = new City(cityName, temperature);
        cityRepository.addCityToTable(city);
    }
    //Удаление города
    @Override
    public void deleteCityByName(String cityName) {
        cityRepository.deleteCityByName(cityName);
    }

//    @Override
//    public void addCityIfNotExists(String cityName, Float temperature) {
//        if (!cityRepository.existsByCity(cityName)) {
//            City city = new City(cityName, temperature);
//            cityRepository.addCityToTable(city);
//            System.out.println("Город " + cityName + " успешно добавлен");
//        } else {
//            System.out.println("Город " + cityName + " уже существует");
//        }
//    }




//
//    @Override
//    public void create(String temperature, String cityName) {
//
//    }
//
//    @Override
//    public String deleteCity(String cityName) {
//        return "";
//    }
//    private List<City> CITIES = Stream.of(
//            new City(1L, 32.1F, "Bereza"),
//            new City(2L, 28.5F, "Minsk"),
//            new City(3L, 25.8F, "Moscow")
//    ).collect(Collectors.toList());

//    @Override
//    public List<City> getAllCities() {
//        return CITIES;
//    }
//
//    @Override
//    public City getByCityName(String cityName) {
//        return CITIES.stream().filter(city -> city.getCityName().equals(cityName))
//                .findFirst()
//                .orElse(new City("Город не найден"));
//    }
//
//
//    @Override
//    public void create(String temperature, String cityName ) {
//        this.CITIES.add(getCity(temperature, cityName));
//    }

//    private City getCity(String temperature, String cityName) {
//        City city = new City();
//        long maxId = getMaxId();
//        city.setId(maxId + 1L);
//        city.setTemperature(Float.valueOf(temperature));
//        city.setCityName(cityName);
//        return city;
//    }

//    private long getMaxId() {
//        long maxId = CITIES.stream()
//                .mapToLong(City::getId)
//                .max()
//                .orElse(0L);
//        return maxId;
//    }
//
//
//    @Override
//    public String deleteCity(String cityName) {
//        boolean wasRemoved = this.CITIES.removeIf(x -> x.getCityName().equalsIgnoreCase(cityName));
//
//        if (wasRemoved) {
//            return "Город '" + cityName + "' успешно удален";
//        } else {
//            return "Город '" + cityName + "' не найден. Удаление не выполнено";
//        }
//    }
}