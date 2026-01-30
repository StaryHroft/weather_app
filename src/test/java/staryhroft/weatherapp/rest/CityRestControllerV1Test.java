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
import staryhroft.weatherapp.entity.Status;
import staryhroft.weatherapp.service.CityService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CityRestControllerV1.class)
class CityRestControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CityService cityService;

    private CityDto testCityDto;
    private FavoriteDto favoriteDto;

    @BeforeEach
    void setUp() {
        testCityDto = CityDto.builder()
                .id(1L)
                .name("Moscow")
                .temperature(15.5)
                .status(Status.FAVORITE)
                .updatedAt(LocalDateTime.now())
                .build();

        favoriteDto = FavoriteDto.builder()
                .cityName("Moscow")
                .status(Status.FAVORITE)
                .message("Город Moscow добавлен в избранное")
                .build();
    }

    @Test//GET /cities - получить все города
    void getAllCitiesRequest_ShouldReturnListOfCities() throws Exception {
        // Given
        List<CityDto> cities = Arrays.asList(
                testCityDto,
                CityDto.builder()
                        .id(2L)
                        .name("London")
                        .temperature(12.0)
                        .status(Status.NOT_FAVORITE)
                        .updatedAt(LocalDateTime.now())
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
                .andExpect(jsonPath("$[0].status").value("FAVORITE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("London"))
                .andExpect(jsonPath("$[1].temperature").value(12.0))
                .andExpect(jsonPath("$[1].status").value("NOT_FAVORITE"));

        verify(cityService, times(1)).getAllCitiesSorted();
    }

    @Test//GET /cities/{name} - успешный поиск города
    void getCityByNameRequest_WhenCityExists_ShouldReturnCity() throws Exception {
        // Given
        when(cityService.getByCityName("Moscow")).thenReturn(testCityDto);

        // When & Then
        mockMvc.perform(get("/cities/Moscow")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Moscow"))
                .andExpect(jsonPath("$.temperature").value(15.5))
                .andExpect(jsonPath("$.status").value("FAVORITE"));

        verify(cityService, times(1)).getByCityName("Moscow");
    }

    @ParameterizedTest//GET /cities/{name} - пустое имя города
    @NullAndEmptySource
    void getCityByNameRequest_WithInvalidName_ShouldReturnBadRequest(String cityName) throws Exception {
        // Given
        String path = cityName == null ? "/cities/" : "/cities/" + cityName;

        // When & Then
        mockMvc.perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test//PATCH /cities/{cityName}/favorite - добавить в избранное
    void setCityAsFavoriteRequest_ShouldReturnFavoriteDto() throws Exception {
        // Given
        when(cityService.setCityAsFavorite("Moscow")).thenReturn(favoriteDto);

        // When & Then
        mockMvc.perform(patch("/cities/Moscow/favorite")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cityName").value("Moscow"))
                .andExpect(jsonPath("$.status").value("FAVORITE"))
                .andExpect(jsonPath("$.message").value("Город Moscow добавлен в избранное"));

        verify(cityService, times(1)).setCityAsFavorite("Moscow");
    }

    @Test//PATCH /cities/{cityName}/favorite/remove - убрать из избранного
    void removeCityFromFavoritesRequest_ShouldReturnFavoriteDto() throws Exception {
        // Given
        FavoriteDto removedFavorite = FavoriteDto.builder()
                .cityName("Moscow")
                .status(Status.NOT_FAVORITE)
                .message("Город Moscow удален из избранного")
                .build();

        when(cityService.removeCityFromFavorites("Moscow")).thenReturn(removedFavorite);

        // When & Then
        mockMvc.perform(patch("/cities/Moscow/favorite/remove")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cityName").value("Moscow"))
                .andExpect(jsonPath("$.status").value("NOT_FAVORITE"))
                .andExpect(jsonPath("$.message").value("Город Moscow удален из избранного"));

        verify(cityService, times(1)).removeCityFromFavorites("Moscow");
    }

    @Test//DELETE /cities/{name} - удалить город
    void deleteCityRequest_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(cityService).deleteCity("Moscow");

        // When & Then
        mockMvc.perform(delete("/cities/Moscow")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cityService, times(1)).deleteCity("Moscow");
    }

    @Test//GET /cities/count - получить количество городов
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

    @Test//DELETE /cities - удалить все города
    void deleteAllCitiesRequest_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(cityService).deleteAllCities();

        // When & Then
        mockMvc.perform(delete("/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cityService, times(1)).deleteAllCities();
    }

    @Test//Пустой список городов
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

    @Test//Специальные символы в названии города
    void getCityByNameRequest_WithSpecialCharacters_ShouldWork() throws Exception {
        // Given
        String cityName = "New York";
        CityDto cityWithSpecialName = CityDto.builder()
                .id(3L)
                .name(cityName)
                .temperature(20.0)
                .build();

        when(cityService.getByCityName(cityName)).thenReturn(cityWithSpecialName);

        // When & Then
        mockMvc.perform(get("/cities/New York")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New York"));

        verify(cityService, times(1)).getByCityName(cityName);
    }
}
