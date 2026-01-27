package staryhroft.weatherapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cities")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    @Column(name = "city_name", nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "temperature", precision = 4, scale = 1, nullable = false)
    private BigDecimal temperature;

    @Column(name = "is_favorite", nullable = false)
    private Boolean favorite = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_weather_update")
    private LocalDateTime lastWeatherUpdate;

    private LocalDateTime temperatureUpdatedAt;


    protected City(Long id, String name, BigDecimal temperature, Boolean favorite) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.favorite = favorite;
    }

    public City(String name, BigDecimal temperature) {
        this.name = name;
        this.temperature = temperature;
        this.favorite = false;
    }


    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean o) {
    }

    public void setWeatherDescription(String ясно) {
    }

    public void setHumidity(int i) {
    }

    public void setWindSpeed(double v) {

    }
    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public void setTemperature(Double temperature) {
        setTemperature(temperature != null ?
                BigDecimal.valueOf(temperature) : null);
    }
}