package staryhroft.weatherapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityCreateDto {

    @NotBlank(message = "Название города обязательно")
    @Size(min = 2, max = 100, message = "Название должно быть от 2 до 50 символов")
    private String name;

    @NotNull(message = "Температура обязательна")
    @Digits(integer = 3, fraction = 1, message = "Температура должна быть в формате XXX.X")
    private BigDecimal temperature;
}
