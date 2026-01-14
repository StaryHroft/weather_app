package staryhroft.weatherapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "weather_in_cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", nullable = false, length = 50, unique = true)
    private String cityName;

    @Column(name = "temp", nullable = false)
    private Float temperature;

    private String errorMessage;

    public City(String cityName, Float temperature) {
        this.cityName = cityName;
        this.temperature = temperature;

    }

    public City(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
