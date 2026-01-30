package staryhroft.weatherapp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import staryhroft.weatherapp.entity.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Component
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityDto {
    private Long id;
    private String name;
    private Double temperature;
    private Status status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public CityDto() {
    }

    public CityDto(Long id, String name, Double temperature, Status status) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.status = status;
    }

    public CityDto(Long id, String name, Double temperature,
                   Status status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}