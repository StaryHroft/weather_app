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

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    protected City(Long id, String name, Double temperature, Status status) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.status = status;
    }

    public City(String name, Double temperature) {
        this.name = name;
        this.temperature = temperature;
        this.status = Status.NOT_FAVORITE;
    }
    //статус по умолчанию
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
        if (status == null) {
            status = Status.NOT_FAVORITE;
        }
    }
}