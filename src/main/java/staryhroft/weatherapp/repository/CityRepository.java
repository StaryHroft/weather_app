package staryhroft.weatherapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import staryhroft.weatherapp.model.City;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    // Найти все города
    List<City> findAll();

    // Найти город по названию
    Optional<City> findByName(String name);

    // Проверить существование города по названию
    boolean existsByName(String name);

    // Удалить город по названию
    void deleteByName(String name);

    // Проверить существование (без учета регистра)
    boolean existsByNameIgnoreCase(String name);
}