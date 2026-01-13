package staryhroft.weatherapp.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "weather_in_cities") // или другое имя вашей таблицы
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", nullable = false, length = 50, unique = true)
    private String city;

    @Column(name = "temp", nullable = false)
    private Double temperature; // или Float, в зависимости от типа в БД

    public Weather() {}

    public Weather(String city, Double temperature) {
        this.city = city;
        this.temperature = temperature;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }


}
