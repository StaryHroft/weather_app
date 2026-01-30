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
import staryhroft.weatherapp.entity.Status;
import staryhroft.weatherapp.exception.CityNotFoundException;
import staryhroft.weatherapp.exception.WeatherApiException;
import staryhroft.weatherapp.mapper.CityMapper;
import staryhroft.weatherapp.entity.City;
import staryhroft.weatherapp.repository.CityRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hibernate.engine.transaction.internal.jta.JtaStatusHelper.getStatus;
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
                .temperature(15.5)
                .status(Status.FAVORITE)
                .updatedAt(LocalDateTime.now().minusHours(1)) // Обновлено 1 час назад
                .build();

        testCityDto = CityDto.builder()
                .id(1L)
                .name("Moscow")
                .temperature(15.5)
                .status(Status.FAVORITE)
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();

        testWeatherResponse = new WeatherApiResponse();
        testWeatherResponse.setTemperature(18.0);
    }

    @Test//сортировка городов
    void getAllCitiesSorted_ShouldReturnSortedCities() {
        City favoriteCity = City.builder()
                .id(1L)
                .name("Moscow")
                .status(Status.FAVORITE)
                .build();

        City regularCity = City.builder()
                .id(2L)
                .name("London")
                .status(Status.NOT_FAVORITE)
                .build();

        List<City> cities = Arrays.asList(favoriteCity, regularCity);
        when(cityRepository.findAllWithFavoritesFirst()).thenReturn(cities);

        List<CityDto> result = cityService.getAllCitiesSorted();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Moscow", result.get(0).getName());
        assertEquals(Status.FAVORITE, result.get(0).getStatus());
        assertEquals("London", result.get(1).getName());
        assertEquals(Status.NOT_FAVORITE, result.get(1).getStatus());

        verify(cityRepository, times(1)).findAllWithFavoritesFirst();
    }

    @Test//город существует в БД и температура актуальна
    void getWeatherByCityName_WhenCityExistsAndTemperatureActual_ShouldReturnFromDb() {
        String cityName = "Moscow";
        when(cityRepository.findByName(cityName)).thenReturn(testCity);
        when(cityMapper.toDto(testCity)).thenReturn(testCityDto);

        CityDto result = cityService.getByCityName(cityName);

        assertNotNull(result);
        assertEquals("Moscow", result.getName());
        assertEquals(15.5, result.getTemperature());

        verify(cityRepository, times(1)).findByName(cityName);
        verify(weatherApiClient, never()).fetchWeather(anyString());
        verify(cityRepository, never()).save(any());
    }

    @Test//город существует в БД и температура неактуальна
    void getWeatherByCityName_WhenCityExistsButTemperatureOutdated_ShouldUpdateFromApi() {
        String cityName = "Moscow";

        City outdatedCity = City.builder()
                .id(1L)
                .name("Moscow")
                .temperature(10.0)
                .updatedAt(LocalDateTime.now().minusHours(25))
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(outdatedCity);
        when(weatherApiClient.fetchWeather(cityName)).thenReturn(testWeatherResponse);
        when(cityRepository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CityDto updatedDto = CityDto.builder()
                .id(1L)
                .name("Moscow")
                .temperature(18.0)
                .build();
        when(cityMapper.toDto(any(City.class))).thenReturn(updatedDto);

        CityDto result = cityService.getByCityName(cityName);

        assertNotNull(result);
        assertEquals(18.0, result.getTemperature()); // Обновленная температура

        verify(weatherApiClient, times(1)).fetchWeather(cityName);
        verify(cityRepository, times(1)).save(any(City.class));
    }

    @Test//город не существует в БД
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
                .temperature(18.0)
                .build();
        when(cityMapper.toDto(any(City.class))).thenReturn(newCityDto);

        CityDto result = cityService.getByCityName(cityName);

        assertNotNull(result);
        assertEquals("NewCity", result.getName());
        assertEquals(18.0, result.getTemperature());

        verify(cityRepository, times(1)).save(any(City.class));
        verify(weatherApiClient, times(1)).fetchWeather(cityName);
    }

    @Test//присваивание статуса любимый город
    void setCityAsFavorite_WhenCityExistsAndNotFavorite_ShouldAddToFavorites() {
        String cityName = "Moscow";
        City city = City.builder()
                .id(1L)
                .name("Moscow")
                .status(Status.NOT_FAVORITE)
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(city);
        when(cityRepository.countByFavorite()).thenReturn(2L); // Есть 2 избранных
        when(cityRepository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FavoriteDto result = cityService.setCityAsFavorite(cityName);

        assertEquals(Status.FAVORITE, result.getStatus());
        assertEquals("Moscow", result.getCityName());
        assertTrue(result.getMessage().contains("Город добавлен в список любимых городов"));

        verify(cityRepository, times(1)).save(city);
        assertEquals(Status.FAVORITE, result.getStatus());
    }

    @Test//присваивание статуса любимый город, когда такого города нет в БД -> исключение
    void setCityAsFavorite_WhenCityNotFound_ShouldThrowException() {
        String cityName = "UnknownCity";
        when(cityRepository.findByName(cityName)).thenReturn(null);

        // When & Then
        CityNotFoundException exception = assertThrows(CityNotFoundException.class,
                () -> cityService.setCityAsFavorite(cityName));

        assertTrue(exception.getMessage().contains(cityName));
    }

    @Test//удаление из списка любимых
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

    @Test//количество городов
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

    @Test//удаление всех городов
    void deleteAllCities_ShouldDeleteAll() {
        // Given
        doNothing().when(cityRepository).deleteAll();

        // When
        cityService.deleteAllCities();

        // Then
        verify(cityRepository, times(1)).deleteAll();
    }

    @Test//корректная конвертация городов в ДТО
    void convertToDto_ShouldConvertCorrectly() throws Exception {
        // Используем reflection для тестирования приватного метода
        City city = City.builder()
                .id(1L)
                .name("Test")
                .temperature(20.0)
                .status(Status.FAVORITE)
                .updatedAt(LocalDateTime.now())
                .build();

        // Вызываем приватный метод через reflection
        var method = CityServiceImpl.class.getDeclaredMethod("convertToDto", City.class);
        method.setAccessible(true);

        CityDto result = (CityDto) method.invoke(cityService, city);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getName());
        assertEquals(20.0, result.getTemperature());
        assertEquals(Status.FAVORITE, result.getStatus());
    }

    @Test//сортировка городов когда ни один не пустой
    void getAllCitiesSorted_WhenNoCities_ShouldReturnEmptyList() {
        // Given
        when(cityRepository.findAllWithFavoritesFirst()).thenReturn(List.of());

        // When
        List<CityDto> result = cityService.getAllCitiesSorted();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test//погода по названию города, если АПИ не доступен - исключение
    void getWeatherByCityName_WhenApiFails_ShouldPropagateException() {
        // Given
        String cityName = "Moscow";
        City outdatedCity = City.builder()
                .id(1L)
                .name("Moscow")
                .updatedAt(LocalDateTime.now().minusHours(25))
                .build();

        when(cityRepository.findByName(cityName)).thenReturn(outdatedCity);
        when(weatherApiClient.fetchWeather(cityName))
                .thenThrow(new WeatherApiException("API недоступно"));

        // When & Then
        assertThrows(WeatherApiException.class,
                () -> cityService.getByCityName(cityName));
    }
}