package staryhroft.weatherapp.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import staryhroft.weatherapp.model.City;

import java.util.List;

@Repository
public class CityRepositoryImpl implements CityRepository {
    @PersistenceContext
    private EntityManager entityManager;

    // Создание таблицы
    @Transactional
    @Override
    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS weather_in_cities (
                    id BIGSERIAL PRIMARY KEY,
                    city VARCHAR(100) NOT NULL UNIQUE,
                    temp DECIMAL(5,2) NOT NULL
                    )
                """;
        entityManager.createNativeQuery(sql).executeUpdate();
        System.out.println("Таблица 'weather_in_cities' создана");
    }
    // Показать все города из таблицы
    @Override
    public List<City> getAllCities() {
        String sql = "SELECT * FROM weather_in_cities ORDER BY city";
        Query query = entityManager.createNativeQuery(sql, City.class);
        return query.getResultList();
    }
    //Показать город по названию
    @Override
    public City getCityByName(String cityName) {
        try {
            String sql = "SELECT * FROM weather_in_cities WHERE city = ?";
            Query query = entityManager.createNativeQuery(sql, City.class);
            query.setParameter(1, cityName);
            return (City) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Город '" + cityName + "' не найден в базе данных");
            return null;
        }
    }
    // Проверка есть ли такой горол
    @Override
    @Transactional(readOnly = true)
    public boolean existsByCity(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            return false;
        }
        String sql = """
                SELECT EXISTS(
                    SELECT 1 FROM weather_in_cities
                    WHERE city = ?
                    LIMIT 1
                )
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, cityName);

        Boolean exists = (Boolean) query.getSingleResult();
        return Boolean.TRUE.equals(exists);
    }
    // Добавление города
    @Transactional
    @Override
    public void addCityToTable(City city) {
        entityManager.persist(city);//hibernate
        System.out.println("Город " + city.getCityName() + " добавлен с ID: " + city.getId() + " в таблицу 'weather_in_cities'");
    }
    //Удаление города
    @Transactional
    @Override
    public void deleteCityByName(String cityName) {
        String sql = "DELETE FROM weather_in_cities WHERE city = ?";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, cityName);
        query.executeUpdate();
    }
}
