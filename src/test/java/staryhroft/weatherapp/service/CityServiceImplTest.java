package staryhroft.weatherapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import staryhroft.weatherapp.client.WeatherApiClient;
import staryhroft.weatherapp.dto.response.CityDto;
import staryhroft.weatherapp.dto.response.FavoriteDto;
import staryhroft.weatherapp.exception.CityNotFoundException;
import staryhroft.weatherapp.exception.WeatherApiException;
import staryhroft.weatherapp.mapper.CityMapper;
import staryhroft.weatherapp.entity.City;
import staryhroft.weatherapp.repository.CityRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private WeatherApiClient weatherApiClient;

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CityServiceImpl cityService;

    private City testCity;
    private CityDto testCityDto;
    private WeatherApiResponse testWeatherResponse;

    @BeforeEach
    void setUp() {
        testCity = City.builder()
                .id(1L)
                .name("Moscow")
                .temperature(BigDecimal.valueOf(15.5))
                .favorite(true)
                .temperatureUpdatedAt(LocalDateTime.now().minusHours(1)) // Обновлено 1 час назад
                .build();

        testCityDto = CityDto.builder()
                .id(1L)
                .name("Moscow")
                .temperature(BigDecimal.valueOf(15.5))
                .favorite(true)
                .temperatureUpdatedAt(LocalDateTime.now().minusHours(1))
                .build();

        testWeatherResponse = new WeatherApiResponse();
        testWeatherResponse.setTemperature(BigDecimal.valueOf(18.0));
    }

    @Test
    void getAllCitiesSorted_ShouldReturnSortedCities() {
        City favoriteCity = City.builder()
                .id(1L)
                .name("Moscow")
                .favorite(true)
                .build();

        City regularCity = City.builder()
                .id(2L)
                .name("London")
                .favorite(false)
                .build();

        List<City> cities = Arrays.asList(favoriteCity, regularCity);
        when(cityRepository.findAllWithFavoritesFirst()).thenReturn(cities);

        CityDto favoriteDto = CityDto.builder().id(1L).name("Moscow").favorite(true).build();
        CityDto regularDto = CityDto.builder().id(2L).name("London").favorite(false).build();

        //when(cityMapper.toDto(favoriteCity)).thenReturn(favoriteDto);
        //when(cityMapper.toDto(regularCity)).thenReturn(regularDto);

        List<CityDto> result = cityService.getAllCitiesSorted();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Moscow", result.get(0).getName());
        assertTrue(result.get(0).getFavorite());
        assertEquals("London", result.get(1).getName());
        assertFalse(result.get(1).getFavorite());

        verify(cityRepository, times(1)).findAllWithFavoritesFirst();
    }
    //ошибка
    @Test
    void getWeatherByCityName_WhenCityExistsAndTemperatureActual_ShouldReturnFromDb() {
        String cityName = "Moscow";
        when(cityRepository.findByName(cityName)).thenReturn(testCity);
        when(cityMapper.toDto(testCity)).thenReturn(testCityDto);

        CityDto result = cityService.getWeatherByCityName(cityName);

        assertNotNull(result);
        assertEquals("Moscow", result.getName());
        assertEquals(15.5, result.getTemperature());

        verify(cityRepository, times(1)).findByName(cityName);
        verify(weatherApiClient, never()).fetchWeather(anyString());
        verify(cityRepository, never()).save(any());
    }
    //ошибка
    @Test
    void getWeatherByCityName_WhenCityExistsButTemperatureOutdated_ShouldUpdateFromApi() {
        String cityName = "Moscow";

        City outdatedCity = City.builder()
                .id(1L)
                .name("Moscow")
                .temperature(BigDecimal.valueOf(10.0))
                .temperatureUpdatedAt(LocalDateTime.now().minusHours(25))
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(outdatedCity);
        when(weatherApiClient.fetchWeather(cityName)).thenReturn(testWeatherResponse);
        when(cityRepository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CityDto updatedDto = CityDto.builder()
                .id(1L)
                .name("Moscow")
                .temperature(BigDecimal.valueOf(18.0))
                .build();
        when(cityMapper.toDto(any(City.class))).thenReturn(updatedDto);

        CityDto result = cityService.getWeatherByCityName(cityName);

        assertNotNull(result);
        assertEquals(18.0, result.getTemperature()); // Обновленная температура

        verify(weatherApiClient, times(1)).fetchWeather(cityName);
        verify(cityRepository, times(1)).save(any(City.class));
    }
    //ошибка
    @Test
    void getWeatherByCityName_WhenCityNotFound_ShouldCreateFromApi() {
        String cityName = "NewCity";
        when(cityRepository.findByName(cityName)).thenReturn(null);
        when(weatherApiClient.fetchWeather(cityName)).thenReturn(testWeatherResponse);
        when(cityRepository.save(any(City.class))).thenAnswer(invocation -> {
            City savedCity = invocation.getArgument(0);
            savedCity.setId(2L);
            return savedCity;
        });

        CityDto newCityDto = CityDto.builder()
                .id(2L)
                .name("NewCity")
                .temperature(BigDecimal.valueOf(18.0))
                .build();
        when(cityMapper.toDto(any(City.class))).thenReturn(newCityDto);

        CityDto result = cityService.getWeatherByCityName(cityName);

        assertNotNull(result);
        assertEquals("NewCity", result.getName());
        assertEquals(18.0, result.getTemperature());

        verify(cityRepository, times(1)).save(any(City.class));
        verify(weatherApiClient, times(1)).fetchWeather(cityName);
    }
    //ошибка
    @Test
    void setCityAsFavorite_WhenCityExistsAndNotFavorite_ShouldAddToFavorites() {
        String cityName = "Moscow";
        City city = City.builder()
                .id(1L)
                .name("Moscow")
                .favorite(false)
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(city);
        when(cityRepository.countByFavoriteTrue()).thenReturn(2L); // Есть 2 избранных
        when(cityRepository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FavoriteDto result = cityService.setCityAsFavorite(cityName);

        assertTrue(result.getFavorite());
        assertEquals("Moscow", result.getCityName());
        assertTrue(result.getMessage().contains("добавлен в избранное"));

        verify(cityRepository, times(1)).save(city);
        assertTrue(city.getFavorite());
    }
    //ошибка
    @Test
    void setCityAsFavorite_WhenCityAlreadyFavorite_ShouldReturnError() {
        String cityName = "Moscow";
        City city = City.builder()
                .id(1L)
                .name("Moscow")
                .favorite(true) // Уже в избранном
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(city);

        FavoriteDto result = cityService.setCityAsFavorite(cityName);

        assertFalse(result.getFavorite());
        assertEquals("Город уже в избранном", result.getMessage());

        verify(cityRepository, never()).save(any());
    }

    //ошибка
    @Test
    void setCityAsFavorite_WhenMaxFavoritesReached_ShouldReturnError() {
        String cityName = "Moscow";
        City city = City.builder()
                .id(1L)
                .name("Moscow")
                .favorite(false)
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(city);
        when(cityRepository.countByFavoriteTrue()).thenReturn(3L); // Максимум 3 уже есть

        FavoriteDto result = cityService.setCityAsFavorite(cityName);

        assertFalse(result.getFavorite());
        assertTrue(result.getMessage().contains("Нельзя добавить более 3 городов"));

        verify(cityRepository, never()).save(any());
    }

    @Test
    void setCityAsFavorite_WhenCityNotFound_ShouldThrowException() {
        String cityName = "UnknownCity";
        when(cityRepository.findByName(cityName)).thenReturn(null);

        // When & Then
        CityNotFoundException exception = assertThrows(CityNotFoundException.class,
                () -> cityService.setCityAsFavorite(cityName));

        assertTrue(exception.getMessage().contains(cityName));
    }

    /**
     * Тест 9: Удалить город из избранного - успешно
     */
    //ошибка
    @Test
    void removeCityFromFavorites_WhenCityIsFavorite_ShouldRemove() {
        // Given
        String cityName = "Moscow";
        City city = City.builder()
                .id(1L)
                .name("Moscow")
                .favorite(true) // В избранном
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(city);
        when(cityRepository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FavoriteDto result = cityService.removeCityFromFavorites(cityName);

        // Then
        assertFalse(result.getFavorite());
        assertEquals("Moscow", result.getCityName());
        assertTrue(result.getMessage().contains("удален из избранного"));

        verify(cityRepository, times(1)).save(city);
        assertFalse(city.getFavorite());
    }

    /**
     * Тест 10: Удалить город из избранного - город не в избранном
     */
    //ошибка
    @Test
    void removeCityFromFavorites_WhenCityNotFavorite_ShouldReturnError() {
        // Given
        String cityName = "Moscow";
        City city = City.builder()
                .id(1L)
                .name("Moscow")
                .favorite(false) // Не в избранном
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(city);

        // When
        FavoriteDto result = cityService.removeCityFromFavorites(cityName);

        // Then
        assertFalse(result.getFavorite());
        assertEquals("Город отсутствует в списке любимых городов", result.getMessage());

        verify(cityRepository, never()).save(any());
    }

    /**
     * Тест 11: Удалить город - успешно
     */
    @Test
    void deleteCity_WhenCityExists_ShouldDelete() {
        // Given
        String cityName = "Moscow";
        when(cityRepository.existsByName(cityName)).thenReturn(true);
        doNothing().when(cityRepository).deleteByName(cityName);

        // When
        cityService.deleteCity(cityName);

        // Then
        verify(cityRepository, times(1)).existsByName(cityName);
        verify(cityRepository, times(1)).deleteByName(cityName);
    }

    /**
     * Тест 12: Удалить город - город не найден
     */
    @Test
    void deleteCity_WhenCityNotFound_ShouldThrowException() {
        // Given
        String cityName = "UnknownCity";
        when(cityRepository.existsByName(cityName)).thenReturn(false);

        // When & Then
        CityNotFoundException exception = assertThrows(CityNotFoundException.class,
                () -> cityService.deleteCity(cityName));

        assertTrue(exception.getMessage().contains(cityName));
        verify(cityRepository, never()).deleteByName(anyString());
    }

    /**
     * Тест 13: Получить количество городов
     */
    @Test
    void countCities_ShouldReturnCorrectCount() {
        // Given
        long expectedCount = 5L;
        when(cityRepository.count()).thenReturn(expectedCount);

        // When
        long result = cityService.countCities();

        // Then
        assertEquals(expectedCount, result);
        verify(cityRepository, times(1)).count();
    }

    /**
     * Тест 14: Удалить все города
     */
    @Test
    void deleteAllCities_ShouldDeleteAll() {
        // Given
        doNothing().when(cityRepository).deleteAll();

        // When
        cityService.deleteAllCities();

        // Then
        verify(cityRepository, times(1)).deleteAll();
    }

    /**
     * Тест 15: Конвертация города в DTO (приватный метод)
     */
    //ошибка
    @Test
    void convertToDto_ShouldConvertCorrectly() throws Exception {
        // Используем reflection для тестирования приватного метода
        City city = City.builder()
                .id(1L)
                .name("Test")
                .temperature(BigDecimal.valueOf(20.0))
                .favorite(true)
                .temperatureUpdatedAt(LocalDateTime.now())
                .build();

        // Вызываем приватный метод через reflection
        var method = CityServiceImpl.class.getDeclaredMethod("convertToDto", City.class);
        method.setAccessible(true);

        CityDto result = (CityDto) method.invoke(cityService, city);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getName());
        assertEquals(20.0, result.getTemperature());
        assertTrue(result.getFavorite());
    }

    /**
     * Тест 16: Пустой список городов
     */
    @Test
    void getAllCitiesSorted_WhenNoCities_ShouldReturnEmptyList() {
        // Given
        when(cityRepository.findAllWithFavoritesFirst()).thenReturn(List.of());

        // When
        List<CityDto> result = cityService.getAllCitiesSorted();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Тест 17: Ошибка при получении данных от API
     */
    @Test
    void getWeatherByCityName_WhenApiFails_ShouldPropagateException() {
        // Given
        String cityName = "Moscow";
        City outdatedCity = City.builder()
                .id(1L)
                .name("Moscow")
                .temperatureUpdatedAt(LocalDateTime.now().minusHours(25))
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(outdatedCity);
        when(weatherApiClient.fetchWeather(cityName))
                .thenThrow(new WeatherApiException("API недоступно"));

        // When & Then
        assertThrows(WeatherApiException.class,
                () -> cityService.getWeatherByCityName(cityName));
    }

    /**
     * Тест 18: Проверка константы MAX_FAVORITES
     */
    @Test
    void shouldRespectMaxFavoritesConstant() {
        // Проверяем, что лимит действительно равен 3
        assertEquals(3, CityServiceImpl.MAX_FAVORITES);
    }
}