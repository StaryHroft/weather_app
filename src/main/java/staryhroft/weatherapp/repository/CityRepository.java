package staryhroft.weatherapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import staryhroft.weatherapp.entity.City;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    // Показать города: сначала избранные (по id DESC), потом остальные (по id DESC)
    @Query("SELECT c FROM City c ORDER BY c.favorite DESC, c.id DESC")
    List<City> findAllWithFavoritesFirst();

    // Найти город по названию
    City findByName(String name);

    // Проверить существование города по названию
    boolean existsByName(String name);

    //Удалить город по названию
    void deleteByName(String name);

    @Query("SELECT COUNT(c) FROM City c WHERE c.favorite = true")
    long countByFavoriteTrue();
}