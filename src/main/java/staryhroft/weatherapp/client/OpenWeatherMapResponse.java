package staryhroft.weatherapp.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

//парсинг ответа от API
@Data
public class OpenWeatherMapResponse {

    @JsonProperty("main")
    private Main main;

    @Data
    public static class Main {
        @JsonProperty("temp")
        private BigDecimal temp;           // температура
    }
}
