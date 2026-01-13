package staryhroft.weatherapp.repository;

import staryhroft.weatherapp.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {

    Optional<Weather> findByCity(String city);

    List<Weather> findByTemperatureBetween(Double minTemp, Double maxTemp);

    List<Weather> findAllByOrderByTemperatureAsc();  // от холодных к теплым

    List<Weather> findAllByOrderByTemperatureDesc(); // от теплых к холодным

    void deleteByCity(String city);

    boolean existsByCity(String city);
}