package staryhroft.weatherapp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Component
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityDto {
    private Long id;
    private String name;
    private BigDecimal temperature;
    private Boolean favorite;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private LocalDateTime temperatureUpdatedAt;

    public CityDto() {
    }

    public CityDto(Long id, String name, BigDecimal temperature, Boolean favorite) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.favorite = favorite;
    }

    public CityDto(Long id, String name, BigDecimal temperature,
                   Boolean favorite, LocalDateTime createdAt, LocalDateTime updatedAt,
                   LocalDateTime temperatureUpdatedAt) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.favorite = favorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.temperatureUpdatedAt = temperatureUpdatedAt;
    }
}