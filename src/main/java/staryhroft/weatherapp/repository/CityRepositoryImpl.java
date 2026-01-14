//package staryhroft.weatherapp.repository;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//import staryhroft.weatherapp.model.City;
//
//import java.sql.*;
//import java.util.List;
//
//@Repository
//public class CityRepositoryImpl /*implements CityRepository*/ {
//
//    private final JdbcTemplate jdbcTemplate;
//@Autowired
//    public CityRepositoryImpl(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//    // Показать все города из таблицы
//    public List<City> getCities() {
//        return jdbcTemplate.query("SELECT * FROM City", new BeanPropertyRowMapper<>(City.class));
//    }
//    //Показать город по названию
//    public City getCity(String name) {
//    return jdbcTemplate.query("SELECT * FROM City WHERE name=?", new Object[]{name}, new BeanPropertyRowMapper<>(City.class))
//            .stream().findAny().orElse(null);
//        //cities.stream().filter(city -> city.getName() == name).findAny().orElse(null);
//    }
//
//    // Добавить города
//    public void addCity(City city) {
//    jdbcTemplate.update("INSERT INTO City VALUES(1, ?, ?)", city.getName(), city.getTemp());
//    }
//
//    //Удалить города
//    public void deleteCity(String name) {
//    jdbcTemplate.update("DELETE FROM City WHERE name=?", name);
////        cities.removeIf(city -> city.getName() == name);
//    }
//    // Создание таблицу
//    public void createTable() {
//    jdbcTemplate.update("CREATE TABLE IF NOT EXISTS weather_in_cities (\n" +
//            "//                    id BIGSERIAL PRIMARY KEY,\n" +
//            "//                    city VARCHAR(100) NOT NULL UNIQUE,\n" +
//            "//                    temp DECIMAL(5,2) NOT NULL\n" +
//            "//                    )");
//    }
//
//    // Проверка есть ли такой горол
//    public boolean existsByCity(String cityName) {
//    jdbcTemplate.query("SELECT EXISTS(\n" +
//            "//                    SELECT 1 FROM weather_in_cities\n" +
//            "//                    WHERE city = ?\n" +
//            "//                    LIMIT 1\n" +
//            "//                )");
//    }
//
////    // Создание таблицы
////    @Transactional
////    @Override
////    public void createTable() {
////        String sql = """
////                CREATE TABLE IF NOT EXISTS weather_in_cities (
////                    id BIGSERIAL PRIMARY KEY,
////                    city VARCHAR(100) NOT NULL UNIQUE,
////                    temp DECIMAL(5,2) NOT NULL
////                    )
////                """;
////        entityManager.createNativeQuery(sql).executeUpdate();
////        System.out.println("Таблица 'weather_in_cities' создана");
////    }
////    // Показать все города из таблицы
////    @Override
////    public List<City> getAllCities() {
////        String sql = "SELECT * FROM weather_in_cities ORDER BY city";
////        Query query = entityManager.createNativeQuery(sql, City.class);
////        return query.getResultList();
////    }
////    //Показать город по названию
////    @Override
////    public City getCityByName(String cityName) {
////        try {
////            String sql = "SELECT * FROM weather_in_cities WHERE city = ?";
////            Query query = entityManager.createNativeQuery(sql, City.class);
////            query.setParameter(1, cityName);
////            return (City) query.getSingleResult();
////        } catch (Exception e) {
////            System.out.println("Город '" + cityName + "' не найден в базе данных");
////            return null;
////        }
////    }
////    // Проверка есть ли такой горол
////    @Override
////    @Transactional(readOnly = true)
////    public boolean existsByCity(String cityName) {
////        if (cityName == null || cityName.isEmpty()) {
////            return false;
////        }
////        String sql = """
////                SELECT EXISTS(
////                    SELECT 1 FROM weather_in_cities
////                    WHERE city = ?
////                    LIMIT 1
////                )
////                """;
////
////        Query query = entityManager.createNativeQuery(sql);
////        query.setParameter(1, cityName);
////
////        Boolean exists = (Boolean) query.getSingleResult();
////        return Boolean.TRUE.equals(exists);
////    }
//
//
//}
