package staryhroft.weatherapp.repository;

import staryhroft.weatherapp.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByCity(String city);

    List<City> findByTemperatureBetween(Double minTemp, Double maxTemp);

    List<City> findAllByOrderByTemperatureAsc();  // от холодных к теплым

    List<City> findAllByOrderByTemperatureDesc(); // от теплых к холодным

    void deleteByCity(String city);

    boolean existsByCity(String city);
}