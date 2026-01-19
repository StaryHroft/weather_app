package staryhroft.weatherapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import staryhroft.weatherapp.model.City;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    // Показать города: сначала избранные (по id DESC), потом остальные (по id DESC)
    @Query("SELECT c FROM City c ORDER BY c.favorite DESC, c.id DESC")
    List<City> findAllWithFavoritesFirst();

    // Найти город по названию
    Optional<City> findByName(String name);

    // Проверить существование города по названию
    boolean existsByName(String name);

    // Проверить наличие города в списке избранных
    @Query("SELECT COUNT(c) FROM City c WHERE c.favorite = true")
    int countFavoriteCities();

    // Удалить город по названию
    void deleteByName(String name);

    // Проверить существование (без учета регистра)
    boolean existsByNameIgnoreCase(String name);
}