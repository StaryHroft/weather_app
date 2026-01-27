package staryhroft.weatherapp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import staryhroft.weatherapp.exception.WeatherApiException;
import staryhroft.weatherapp.service.WeatherApiResponse;


@Component
@Slf4j
public class WeatherApiClient {

    @Value("${openweather.api.url}")
    private String apiUrl;

    @Value("${openweather.api.key}")
    private String apiKey;

    private final RestClient restClient;

    public WeatherApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public WeatherApiResponse fetchWeather(String cityName) throws WeatherApiException {
        try {
            String url = buildWeatherApiUrl(cityName);

            OpenWeatherMapResponse responseBody = restClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), (request, response) -> {
                        // Обработка HTTP ошибок (не 2xx статус)
                        String errorMessage = "API вернуло ошибку: " + response.getStatusCode();

                        // Можно прочитать тело ошибки
                        try {
                            String responseBodyString = new String(response.getBody().readAllBytes());
                            if (!responseBodyString.isEmpty()) {
                                errorMessage += ". Тело ответа: " + responseBodyString;
                            }
                        } catch (Exception ioException) {
                            // Игнорируем ошибки чтения тела
                        }

                        throw new WeatherApiException(errorMessage);
                    })
                    .body(OpenWeatherMapResponse.class);
            if (responseBody == null) {
                throw new WeatherApiException("Пустой ответ от API");
            }

            return mapToWeatherApiResponse(responseBody);

        } catch (Exception e) {  // Ловим ВСЕ исключения
            // Проверяем, не является ли это уже WeatherApiException
            if (e instanceof WeatherApiException) {
                throw (WeatherApiException) e;
            }
            throw new WeatherApiException("Ошибка при обращении к API погоды: " + e.getMessage(), 500);
        }

    }


    private String buildWeatherApiUrl(String cityName) {
        return UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("q", cityName)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "ru")
                .build()
                .toUriString();
    }

    private WeatherApiResponse mapToWeatherApiResponse(OpenWeatherMapResponse apiResponse) {
        WeatherApiResponse response = new WeatherApiResponse();
        response.setTemperature(apiResponse.getMain().getTemp());
        return response;
    }

}
