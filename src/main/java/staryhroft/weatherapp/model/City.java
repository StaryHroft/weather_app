package staryhroft.weatherapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Column(name = "city_name", nullable = false, length = 50)
    private String name;

    @Column(name = "temperature", precision = 4, scale = 1)
    private BigDecimal temperature;

    @Column(name = "is_favorite", nullable = false)
    private Boolean favorite = Boolean.FALSE;

    public City() {}

    public City(Long id, String name, BigDecimal temperature, Boolean favorite) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.favorite = favorite;
    }

    public City(String name, BigDecimal temperature, Boolean favorite) {
        this.name = name;
        this.temperature = temperature;
        this.favorite = favorite;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}