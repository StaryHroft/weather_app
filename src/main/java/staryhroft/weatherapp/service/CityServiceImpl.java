package staryhroft.weatherapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staryhroft.weatherapp.model.City;
import staryhroft.weatherapp.repository.CityRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }
    //Получить все города из БД
    @Override
    @Transactional(readOnly = true)
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
    // Найти город по названию
    @Override
    @Transactional(readOnly = true)
    public City getCityByName(String name) {
        return cityRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Город '" + name + "' не найден"));
    }
    //Добавить новый город
    @Override
    public City addCity(City city) {
        if (cityRepository.existsByName(city.getName())) {
            throw new RuntimeException("Город '" + city.getName() + "' уже существует");
        }
        return cityRepository.save(city);
    }
    //Удалить город по названию
    @Override
    public void deleteCity(String name) {
        City city = getCityByName(name);
        cityRepository.delete(city);
    }
    //Проверить, существует ли город с указанным названием
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return cityRepository.existsByName(name);
    }
    //Получить общее количество городов в базе
    @Override
    @Transactional(readOnly = true)
    public long countCities() {
        return cityRepository.count();
    }
    //Получить город с максимальной температурой
    @Override
    @Transactional(readOnly = true)
    public City getWarmestCity() {
        List<City> cities = getAllCities();
        if (cities.isEmpty()) {
            throw new RuntimeException("В базе данных нет городов");
        }
        return cities.stream()
                .max(Comparator.comparing(City::getTemperature))
                .orElseThrow(() -> new RuntimeException("Не удалось найти самый теплый город"));
    }
    //Получить город с минимальной температурой
    @Override
    @Transactional(readOnly = true)
    public City getColdestCity() {
        List<City> cities = getAllCities();

        if (cities.isEmpty()) {
            throw new RuntimeException("В базе данных нет городов");
        }
        return cities.stream()
                .min(Comparator.comparing(City::getTemperature))
                .orElseThrow(() -> new RuntimeException("Не удалось найти самый холодный город"));
    }
    //Получить среднюю температуру по всем городам
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageTemperature() {
        List<City> cities = getAllCities();
        if (cities.isEmpty()) {
            throw new RuntimeException("В базе данных нет городов");
        }
        BigDecimal sum = cities.stream()
                .map(City::getTemperature)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(cities.size()), 2, RoundingMode.HALF_UP);
    }
    //Удалить все города из базы данных
    @Override
    public void deleteAllCities() {
        cityRepository.deleteAll();
    }

//@Service
//public class CityServiceImpl /*implements CityService*/{
//    @Autowired
//    private final CityRepository cityRepository;
//
//    public CityServiceImpl(CityRepository cityRepository) {
//        this.cityRepository = cityRepository;
//    }
////    // Создать таблицу
////    @Override
//    public void createTable() {
//        cityRepository.createTable();
//    }
//    // Показать все города из таблицы
//    @Override
//    public List<City> getAllCities() {
//        return cityRepository.getAllCities();
//    }
//    //Показать город по названию
//    @Override
//    public City getCityByName(String cityName) {
//        City city = cityRepository.getCityByName(cityName);
//        if (city == null) {
//            System.out.println("Город '" + cityName + "' не найден");
//        }
//        return city;
//    }
//    //Проверить наличие города в списке
//    @Override
//    public boolean cityExists(String cityName) {
//        return cityRepository.existsByCity(cityName);
//    }
    // Добавление города
//    @Override
//    public void addCity(String cityName, Float temperature) {
//        City city = new City(cityName, temperature);
//        cityRepository.addCityToTable(city);
//    }
//    //Удаление города
//    @Override
//    public void deleteCityByName(String cityName) {
//        cityRepository.deleteCityByName(cityName);
//    }

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