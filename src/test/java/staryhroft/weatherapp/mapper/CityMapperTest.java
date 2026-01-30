package staryhroft.weatherapp.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import staryhroft.weatherapp.dto.response.CityDto;
import staryhroft.weatherapp.entity.City;
import staryhroft.weatherapp.entity.Status;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


class CityMapperTest {

    private CityMapper cityMapper;

    @BeforeEach
    void setUp() {
        cityMapper = new CityMapper();
    }

    @Test//отображает город в ДТО когда все поля
    void shouldMapCityToDto_WhenAllFieldsPresent() throws InterruptedException {
        // Given
        City city = new City();
        city.setId(1L);
        city.setName("Москва");
        city.setTemperature(15.5);
        city.setStatus(Status.FAVORITE);

        TimeUnit.SECONDS.sleep(5);
        LocalDateTime afterSleep = LocalDateTime.now();
        city.setUpdatedAt(afterSleep);
        city.setCreatedAt(afterSleep.minusDays(1));

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Москва", result.getName());
        assertEquals(15.5, result.getTemperature());
        assertEquals(Status.FAVORITE, result.getStatus());
        assertEquals(afterSleep, result.getUpdatedAt());
    }

    @ParameterizedTest//разные температуры
    @ValueSource(doubles = {-50.0, -0.0, 0.0, 0.5, 50.0, 100.0, -273.15})
    void shouldMapCity_WithDifferentTemperatures(Double temperature) {
        City city = new City();
        city.setId(5L);
        city.setName("Тестовый город");
        city.setTemperature(temperature);

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(temperature, result.getTemperature(),
                "Температура должна сохраняться точно: " + temperature);
    }

    @Test//длинное имя
    void shouldMapCity_WithLongName() {
        String longName = "Санкт-Петербург-на-Неве-в-Ленинградской-области";
        City city = new City();
        city.setId(7L);
        city.setName(longName);
        city.setTemperature(12.0);

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(longName, result.getName(),
                "Длинное имя должно сохраняться полностью");
    }

    @Test//двойная точность температуры
    void shouldPreserveDoublePrecision() {
        double preciseTemperature = 15.5123456789;
        City city = new City();
        city.setId(9L);
        city.setName("Точный город");
        city.setTemperature(preciseTemperature);

        CityDto result = cityMapper.toDto(city);

        assertEquals(preciseTemperature, result.getTemperature(),
                "Температура должна сохраняться с точностью double");
    }
}
