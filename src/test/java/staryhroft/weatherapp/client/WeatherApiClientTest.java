package staryhroft.weatherapp.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import staryhroft.weatherapp.exception.WeatherApiException;
import staryhroft.weatherapp.service.WeatherApiResponse;

import java.math.BigDecimal;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class WeatherApiClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private WeatherApiClient weatherApiClient;


    @BeforeEach
    void setUp() throws Exception {
        weatherApiClient = new WeatherApiClient(restClient);

        setField(weatherApiClient, "apiUrl", "https://api.openweathermap.org/data/2.5/weather");
        setField(weatherApiClient, "apiKey", "test-api-key");
        lenient().when(restClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(Predicate.class), any(RestClient.ResponseSpec.ErrorHandler.class)))
                .thenReturn(responseSpec);
    }

    @Test//успешный запрос
    void shouldFetchWeatherSuccessfully() {
        String cityName = "Moscow";
        OpenWeatherMapResponse mockResponse = createMockResponse(15.5);

        when(responseSpec.body(OpenWeatherMapResponse.class)).thenReturn(mockResponse);

        WeatherApiResponse result = weatherApiClient.fetchWeather(cityName);

        assertNotNull(result);
        assertEquals(15.5, result.getTemperature());

        verify(requestHeadersUriSpec).uri(expectedUrlForCity(cityName));
        verify(responseSpec).body(OpenWeatherMapResponse.class);

    }

    @Test//город не найден
    void shouldThrowExceptionWhenCityNotFound() {
        String cityName = "UnknownCity";
        when(responseSpec.body(OpenWeatherMapResponse.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "City not found"));

        WeatherApiException exception = assertThrows(WeatherApiException.class,
                () -> weatherApiClient.fetchWeather(cityName));

        assertThat(exception.getMessage()).contains("Ошибка при обращении к API погоды");
    }

    @Test//ключ не действителен
    void shouldThrowExceptionWhenApiKeyInvalid() {
        String cityName = "Moscow";

        when(responseSpec.body(OpenWeatherMapResponse.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid API key"));

        WeatherApiException exception = assertThrows(WeatherApiException.class,
                () -> weatherApiClient.fetchWeather(cityName));

        assertThat(exception.getMessage()).contains("Ошибка при обращении к API погоды");
    }

    @Test//тело ответа = 0
    void shouldThrowExceptionWhenResponseBodyIsNull() {
        String cityName = "Moscow";

        when(responseSpec.body(OpenWeatherMapResponse.class)).thenReturn(null);

        WeatherApiException exception = assertThrows(WeatherApiException.class,
                () -> weatherApiClient.fetchWeather(cityName));

        assertEquals("Пустой ответ от API", exception.getMessage());
    }

    @Test//правильное отображение ответа
    void shouldMapApiResponseCorrectly() throws Exception {
        OpenWeatherMapResponse apiResponse = createMockResponse(22.5);

        WeatherApiResponse result = invokePrivateMethod("mapToWeatherApiResponse", apiResponse);

        assertNotNull(result);
        assertEquals(22.5, result.getTemperature());
    }

    @Test//разные города
    void shouldWorkWithDifferentCities() {
        String[] cities = {"Moscow", "London", "Paris", "Berlin"};

        for (String city : cities) {
            OpenWeatherMapResponse mockResponse = createMockResponse(15.0 + Math.random() * 10);
            when(responseSpec.body(OpenWeatherMapResponse.class)).thenReturn(mockResponse);
            clearInvocations(requestHeadersUriSpec);
            WeatherApiResponse result = weatherApiClient.fetchWeather(city);

            assertNotNull(result);
            assertNotNull(result.getTemperature());

            verify(requestHeadersUriSpec).uri(expectedUrlForCity(city));
        }
    }

    // ========== Вспомогательные методы ==========

    private OpenWeatherMapResponse createMockResponse(double temperature) {
        OpenWeatherMapResponse response = new OpenWeatherMapResponse();
        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemp(temperature);
        response.setMain(main);
        return response;
    }

    private String expectedUrlForCity(String cityName) {
        return UriComponentsBuilder.fromUriString("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("q", cityName)
                .queryParam("appid", "test-api-key")
                .queryParam("units", "metric")
                .queryParam("lang", "ru")
                .build()
                .toUriString();
    }

    private <T> T invokePrivateMethod(String methodName, Object... args) throws Exception {
        var method = WeatherApiClient.class.getDeclaredMethod(methodName,
                getParameterTypes(args));
        method.setAccessible(true);
        return (T) method.invoke(weatherApiClient, args);
    }

    private Class<?>[] getParameterTypes(Object... args) {
        return java.util.Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class[]::new);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}