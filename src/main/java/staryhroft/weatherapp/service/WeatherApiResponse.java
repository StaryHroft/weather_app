package staryhroft.weatherapp.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WeatherApiResponse {
    private BigDecimal temperature;
}