package staryhroft.weatherapp.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AddCityRequest {
//    @NotBlank(message = "Заголовок 'Name' обязателен")
//    @Size(min = 2, max = 50)
//    private String name;
//
//    @NotNull(message = "Заголовок 'Temp' обязателен")
//    @Digits(integer = 3, fraction = 1)
//    private BigDecimal temp;
//
}
