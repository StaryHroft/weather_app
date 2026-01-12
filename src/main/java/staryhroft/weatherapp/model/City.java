package staryhroft.weatherapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {
    private Long id;
    private Float temperature;
    private String cityName;
    private String errorMessage;

    public City(Long id, Float temperature, String cityName) {
        this.id = id;
        this.temperature = temperature;
        this.cityName = cityName;
    }

    public City(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
