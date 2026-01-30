package staryhroft.weatherapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import staryhroft.weatherapp.entity.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriteDto {
    private String message;
    private Long cityId;
    private String cityName;
    private Status status;

    public static FavoriteDto added(Long cityId, String cityName) {
        return new FavoriteDto(
                "Город добавлен в список любимых городов",
                cityId,
                cityName,
                Status.FAVORITE
        );
    }

    public static FavoriteDto removed(Long cityId, String cityName) {
        return new FavoriteDto(
                "Город удален из списка любимых городов",
                cityId,
                cityName,
                Status.NOT_FAVORITE
        );
    }

    public static FavoriteDto error(String message) {
        FavoriteDto dto = new FavoriteDto();
        dto.setMessage(message);
        return dto;
    }
}
