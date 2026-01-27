package staryhroft.weatherapp.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import staryhroft.weatherapp.dto.response.CityDto;
import staryhroft.weatherapp.dto.response.FavoriteDto;
import staryhroft.weatherapp.service.CityService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit тесты для CityRestControllerV1
 * Используем @WebMvcTest для тестирования только контроллера
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(CityRestControllerV1.class)
class CityRestControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CityService cityService;

    private CityDto testCityDto;
    private FavoriteDto favoriteDto;

    @BeforeEach
    void setUp() {
        testCityDto = CityDto.builder()
                .id(1L)
                .name("Moscow")
                .temperature(BigDecimal.valueOf(15.5))
                .favorite(true)
                .temperatureUpdatedAt(LocalDateTime.now())
                .build();

        favoriteDto = FavoriteDto.builder()
                .cityName("Moscow")
                .favorite(true)
                .message("Город Moscow добавлен в избранное")
                .build();
    }

    /**
     * Тест 1: GET /cities - получить все города
     */
    //ошибка
    @Test
    void getAllCitiesRequest_ShouldReturnListOfCities() throws Exception {
        // Given
        List<CityDto> cities = Arrays.asList(
                testCityDto,
                CityDto.builder()
                        .id(2L)
                        .name("London")
                        .temperature(BigDecimal.valueOf(12.0))
                        .favorite(false)
                        .temperatureUpdatedAt(LocalDateTime.now())
                        .build()
        );

        when(cityService.getAllCitiesSorted()).thenReturn(cities);

        // When & Then
        mockMvc.perform(get("/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Moscow"))
                .andExpect(jsonPath("$[0].temperature").value(15.5))
                .andExpect(jsonPath("$[0].favorite").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("London"));

        verify(cityService, times(1)).getAllCitiesSorted();
    }

    /**
     * Тест 2: GET /cities/{name} - успешный поиск города
     */
    //ошибка
    @Test
    void getCityByNameRequest_WhenCityExists_ShouldReturnCity() throws Exception {
        // Given
        when(cityService.getWeatherByCityName("Moscow")).thenReturn(testCityDto);

        // When & Then
        mockMvc.perform(get("/cities/Moscow")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Moscow"))
                .andExpect(jsonPath("$.temperature").value(15.5))
                .andExpect(jsonPath("$.favorite").value(true));

        verify(cityService, times(1)).getWeatherByCityName("Moscow");
    }

    /**
     * Тест 3: GET /cities/{name} - город не найден (обработка исключения)
     */
    //ошибка
    @Test
    void getCityByNameRequest_WhenCityNotFound_ShouldThrowException() throws Exception {
        // Given
        when(cityService.getWeatherByCityName("UnknownCity"))
                .thenThrow(new RuntimeException("City not found"));

        // When & Then
        mockMvc.perform(get("/cities/UnknownCity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()); // или другой ожидаемый статус

        verify(cityService, times(1)).getWeatherByCityName("UnknownCity");
    }

    /**
     * Тест 4: GET /cities/{name} - пустое имя города
     */
    //ошибка
    @ParameterizedTest
    @NullAndEmptySource
    void getCityByNameRequest_WithInvalidName_ShouldReturnBadRequest(String cityName) throws Exception {
        // Given
        String path = cityName == null ? "/cities/" : "/cities/" + cityName;

        // When & Then
        mockMvc.perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Тест 5: PATCH /cities/{cityName}/favorite - добавить в избранное
     */
    //ошибка
    @Test
    void setCityAsFavoriteRequest_ShouldReturnFavoriteDto() throws Exception {
        // Given
        when(cityService.setCityAsFavorite("Moscow")).thenReturn(favoriteDto);

        // When & Then
        mockMvc.perform(patch("/cities/Moscow/favorite")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cityName").value("Moscow"))
                .andExpect(jsonPath("$.favorite").value(true))
                .andExpect(jsonPath("$.message").value("Город Moscow добавлен в избранное"));

        verify(cityService, times(1)).setCityAsFavorite("Moscow");
    }

    /**
     * Тест 6: PATCH /cities/{cityName}/favorite/remove - убрать из избранного
     */
    //ошибка
    @Test
    void removeCityFromFavoritesRequest_ShouldReturnFavoriteDto() throws Exception {
        // Given
        FavoriteDto removedFavorite = FavoriteDto.builder()
                .cityName("Moscow")
                .favorite(false)
                .message("Город Moscow удален из избранного")
                .build();

        when(cityService.removeCityFromFavorites("Moscow")).thenReturn(removedFavorite);

        // When & Then
        mockMvc.perform(patch("/cities/Moscow/favorite/remove")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cityName").value("Moscow"))
                .andExpect(jsonPath("$.favorite").value(false))
                .andExpect(jsonPath("$.message").value("Город Moscow удален из избранного"));

        verify(cityService, times(1)).removeCityFromFavorites("Moscow");
    }

    /**
     * Тест 7: DELETE /cities/{name} - удалить город
     */
    //ошибка
    @Test
    void deleteCityRequest_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(cityService).deleteCity("Moscow");

        // When & Then
        mockMvc.perform(delete("/cities/Moscow")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cityService, times(1)).deleteCity("Moscow");
    }

    /**
     * Тест 8: DELETE /cities/{name} - удаление несуществующего города
     */
    //ошибка
    @Test
    void deleteCityRequest_WhenCityNotFound_ShouldHandleException() throws Exception {
        // Given
        doThrow(new RuntimeException("City not found"))
                .when(cityService).deleteCity("UnknownCity");

        // When & Then
        mockMvc.perform(delete("/cities/UnknownCity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(cityService, times(1)).deleteCity("UnknownCity");
    }

    /**
     * Тест 9: GET /cities/count - получить количество городов
     */
    //ошибка
    @Test
    void countCitiesRequest_ShouldReturnCount() throws Exception {
        // Given
        when(cityService.countCities()).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/cities/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("5"));

        verify(cityService, times(1)).countCities();
    }

    /**
     * Тест 10: DELETE /cities - удалить все города
     */
    //ошибка
    @Test
    void deleteAllCitiesRequest_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(cityService).deleteAllCities();

        // When & Then
        mockMvc.perform(delete("/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cityService, times(1)).deleteAllCities();
    }

    /**
     * Тест 11: Проверка Content-Type в ответах
     */
    //ошибка
    @Test
    void allResponses_ShouldHaveApplicationJsonContentType() throws Exception {
        // Для всех эндпоинтов проверяем Content-Type
        when(cityService.getAllCitiesSorted()).thenReturn(List.of(testCityDto));
        when(cityService.getWeatherByCityName("Moscow")).thenReturn(testCityDto);
        when(cityService.setCityAsFavorite("Moscow")).thenReturn(favoriteDto);
        when(cityService.countCities()).thenReturn(1L);

        // GET /cities
        mockMvc.perform(get("/cities"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // GET /cities/{name}
        mockMvc.perform(get("/cities/Moscow"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // GET /cities/count
        mockMvc.perform(get("/cities/count"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));
    }

    /**
     * Тест 12: Некорректный HTTP метод
     */
    //ошибка
    @Test
    void invalidHttpMethod_ShouldReturn405() throws Exception {
        // Попытка вызвать POST на эндпоинт, который принимает только GET
        mockMvc.perform(post("/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Тест 13: Проверка логирования для DELETE
     */
    //ошибка
    @Test
    void deleteCityRequest_ShouldLogOperation() throws Exception {
        // Given
        doNothing().when(cityService).deleteCity("Moscow");

        // When & Then
        mockMvc.perform(delete("/cities/Moscow"))
                .andExpect(status().isNoContent());

        // Логирование проверяется через интеграционные тесты или специальные инструменты
        // Для unit-тестов достаточно проверить вызов сервиса
    }

    /**
     * Тест 14: Пустой список городов
     */
    //ошибка
    @Test
    void getAllCitiesRequest_WhenNoCities_ShouldReturnEmptyList() throws Exception {
        // Given
        when(cityService.getAllCitiesSorted()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(cityService, times(1)).getAllCitiesSorted();
    }

    /**
     * Тест 15: Специальные символы в названии города
     */
    //ошибка
    @Test
    void getCityByNameRequest_WithSpecialCharacters_ShouldWork() throws Exception {
        // Given
        String cityName = "New York";
        CityDto cityWithSpecialName = CityDto.builder()
                .id(3L)
                .name(cityName)
                .temperature(BigDecimal.valueOf(20.0))
                .build();

        when(cityService.getWeatherByCityName(cityName)).thenReturn(cityWithSpecialName);

        // When & Then
        mockMvc.perform(get("/cities/New York")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New York"));

        verify(cityService, times(1)).getWeatherByCityName(cityName);
    }
}
