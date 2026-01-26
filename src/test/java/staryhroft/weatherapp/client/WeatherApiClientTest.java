package staryhroft.weatherapp.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import staryhroft.weatherapp.exception.WeatherApiException;
import staryhroft.weatherapp.service.WeatherApiResponse;

import java.math.BigDecimal;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    private WeatherApiClient weatherApiClient;

    @BeforeEach
    void setUp() throws Exception {
        weatherApiClient = new WeatherApiClient(restClient);

        setField(weatherApiClient, "apiUrl", "https://api.openweathermap.org/data/2.5/weather");
        setField(weatherApiClient, "apiKey", "test-api-key");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }


    @Test
    void shouldFetchWeatherSuccessfully() {
        String cityName = "Moscow";
        OpenWeatherMapResponse mockResponse = createMockResponse(15.5);

        when(responseSpec.body(OpenWeatherMapResponse.class)).thenReturn(mockResponse);

        WeatherApiResponse result = weatherApiClient.fetchWeather(cityName);

        assertNotNull(result);
        assertEquals(new BigDecimal("15.5"), result.getTemperature());

        verify(requestHeadersUriSpec).uri(expectedUrlForCity(cityName));
    }


    @Test
    void shouldThrowExceptionWhenCityNotFound() {
        String cityName = "UnknownCity";
        when(responseSpec.body(OpenWeatherMapResponse.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "City not found"));

        WeatherApiException exception = assertThrows(WeatherApiException.class,
                () -> weatherApiClient.fetchWeather(cityName));

        assertThat(exception.getMessage()).contains("API вернуло ошибку");
    }

    @Test
    void shouldThrowExceptionWhenApiKeyInvalid() {
        String cityName = "Moscow";

        when(responseSpec.body(OpenWeatherMapResponse.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid API key"));

        WeatherApiException exception = assertThrows(WeatherApiException.class,
                () -> weatherApiClient.fetchWeather(cityName));

        assertThat(exception.getMessage()).contains("API вернуло ошибку: 401");
    }

    @Test
    void shouldThrowExceptionWhenResponseBodyIsNull() {
        String cityName = "Moscow";

        when(responseSpec.body(OpenWeatherMapResponse.class)).thenReturn(null);

        WeatherApiException exception = assertThrows(WeatherApiException.class,
                () -> weatherApiClient.fetchWeather(cityName));

        assertEquals("Пустой ответ от API", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionOnNetworkError() {
        String cityName = "Moscow";

        when(responseSpec.body(OpenWeatherMapResponse.class))
                .thenThrow(new RuntimeException("Connection timeout"));

        WeatherApiException exception = assertThrows(WeatherApiException.class,
                () -> weatherApiClient.fetchWeather(cityName));

        assertThat(exception.getMessage()).contains("Ошибка при обращении к API погоды");
        assertNotNull(exception.getCause());
    }

    @Test
    void shouldBuildCorrectApiUrl() throws Exception {
        String cityName = "Moscow";

        String url = invokePrivateMethod("buildWeatherApiUrl", cityName);

        assertThat(url).isEqualTo(
                "https://api.openweathermap.org/data/2.5/weather" +
                        "?q=Moscow&appid=test-api-key&units=metric&lang=ru"
        );
    }

    @Test
    void shouldMapApiResponseCorrectly() throws Exception {
        OpenWeatherMapResponse apiResponse = createMockResponse(22.5);

        WeatherApiResponse result = invokePrivateMethod("mapToWeatherApiResponse", apiResponse);

        assertNotNull(result);
        assertEquals(new BigDecimal("22.5"), result.getTemperature());
    }

    @Test
    void shouldHandleNullTemperatureInResponse() {
        String cityName = "Moscow";
        OpenWeatherMapResponse mockResponse = new OpenWeatherMapResponse();
        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemp(null);
        mockResponse.setMain(main);

        when(responseSpec.body(OpenWeatherMapResponse.class)).thenReturn(mockResponse);

        WeatherApiResponse result = weatherApiClient.fetchWeather(cityName);

        assertNotNull(result);
        assertNull(result.getTemperature());
    }

    @Test
    void shouldWorkWithDifferentCities() {
        String[] cities = {"Moscow", "London", "Paris", "Berlin"};

        for (String city : cities) {
            reset(restClient, requestHeadersUriSpec, requestHeadersSpec, responseSpec);
            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(expectedUrlForCity(city))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

            OpenWeatherMapResponse mockResponse = createMockResponse(15.0 + Math.random() * 10);
            when(responseSpec.body(OpenWeatherMapResponse.class)).thenReturn(mockResponse);

            WeatherApiResponse result = weatherApiClient.fetchWeather(city);

            assertNotNull(result);
            assertNotNull(result.getTemperature());

            verify(requestHeadersUriSpec).uri(expectedUrlForCity(city));
        }
    }

    @Test
    void shouldHandleRateLimitError() {
        String cityName = "Moscow";

        when(responseSpec.body(OpenWeatherMapResponse.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));

        WeatherApiException exception = assertThrows(WeatherApiException.class,
                () -> weatherApiClient.fetchWeather(cityName));

        assertThat(exception.getMessage()).contains("429");
    }

    @Test
    void shouldHandleRestClientChainException() {
        String cityName = "Moscow";

        when(restClient.get()).thenThrow(new RuntimeException("RestClient error"));

        WeatherApiException exception = assertThrows(WeatherApiException.class,
                () -> weatherApiClient.fetchWeather(cityName));

        assertThat(exception.getMessage()).contains("Ошибка при обращении к API погоды");
    }

    // ========== Вспомогательные методы ==========

    private OpenWeatherMapResponse createMockResponse(double temperature) {
        OpenWeatherMapResponse response = new OpenWeatherMapResponse();
        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemp(BigDecimal.valueOf(temperature));
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