package staryhroft.weatherapp.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import staryhroft.weatherapp.dto.response.CityDto;
import staryhroft.weatherapp.model.City;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


class CityMapperTest {

    private CityMapper cityMapper;

    @BeforeEach
    void setUp() {
        cityMapper = new CityMapper();
    }


    @Test
    void shouldMapCityToDto_WhenAllFieldsPresent() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        City city = new City();
        city.setId(1L);
        city.setName("Москва");
        city.setTemperature(BigDecimal.valueOf(15.5));
        city.setFavorite(true);
        city.setTemperatureUpdatedAt(now);
        city.setCreatedAt(LocalDateTime.now().minusDays(1));
        city.setUpdatedAt(LocalDateTime.now());

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Москва", result.getName());
        assertEquals(15.5, result.getTemperature());
        assertTrue(result.getFavorite());
        assertEquals(now, result.getTemperatureUpdatedAt());
    }

    @Test
    void shouldReturnNull_WhenCityIsNull() {
        CityDto result = cityMapper.toDto(null);

        assertNull(result, "Метод должен возвращать null при null входном значении");
    }

    @Test
    void shouldMapCity_WhenTemperatureIsNull() {
        City city = new City();
        city.setId(2L);
        city.setName("Санкт-Петербург");
        city.setTemperature(null);
        city.setFavorite(false);

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Санкт-Петербург", result.getName());
        assertNull(result.getTemperature(), "Температура должна быть null");
        assertFalse(result.getFavorite());
    }

    @Test
    void shouldMapCity_WhenTemperatureUpdatedAtIsNull() {
        City city = new City();
        city.setId(3L);
        city.setName("Казань");
        city.setTemperature(BigDecimal.valueOf(10.0));
        city.setTemperatureUpdatedAt(null);  // null время обновления

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Казань", result.getName());
        assertEquals(10.0, result.getTemperature());
        assertNull(result.getTemperatureUpdatedAt(), "Время обновления должно быть null");
    }

    @Test
    void shouldMapCity_WhenFavoriteIsNull() {
        // Given
        City city = new City();
        city.setId(4L);
        city.setName("Новосибирск");
        city.setTemperature(BigDecimal.valueOf(5.0));
        city.setFavorite(null);  // null вместо true/false

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("Новосибирск", result.getName());
        assertEquals(5.0, result.getTemperature());
        assertNull(result.getFavorite(), "Флаг избранного должен быть null");
    }

    @Test
    void shouldMapCity_WhenIdIsNull() {
        City city = new City();
        city.setId(null);  // null ID (новый город, еще не сохранен в БД)
        city.setName("Екатеринбург");
        city.setTemperature(BigDecimal.valueOf(8.0));

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertNull(result.getId(), "ID должен быть null");
        assertEquals("Екатеринбург", result.getName());
        assertEquals(8.0, result.getTemperature());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-50.0, -0.0, 0.0, 0.5, 50.0, 100.0, -273.15})
    void shouldMapCity_WithDifferentTemperatures(Double temperature) {
        City city = new City();
        city.setId(5L);
        city.setName("Тестовый город");
        city.setTemperature(BigDecimal.valueOf(temperature));

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(temperature, result.getTemperature(),
                "Температура должна сохраняться точно: " + temperature);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    void shouldMapCity_WithEmptyOrBlankName(String name) {
        City city = new City();
        city.setId(6L);
        city.setName(name);
        city.setTemperature(BigDecimal.valueOf(20.0));

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(name, result.getName(),
                "Имя должно сохраняться как есть, даже если пустое: '" + name + "'");
    }


    @Test
    void shouldMapCity_WithLongName() {
        String longName = "Санкт-Петербург-на-Неве-в-Ленинградской-области";
        City city = new City();
        city.setId(7L);
        city.setName(longName);
        city.setTemperature(BigDecimal.valueOf(12.0));

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(longName, result.getName(),
                "Длинное имя должно сохраняться полностью");
    }

    @Test
    void shouldNotCopyFields_ThatAreNotInDto() {
        City city = new City();
        city.setId(8L);
        city.setName("Владивосток");
        city.setTemperature(BigDecimal.valueOf(7.0));

        city.setCreatedAt(LocalDateTime.now().minusDays(10));
        city.setUpdatedAt(LocalDateTime.now());
        city.setWeatherDescription("ясно");
        city.setHumidity(75);
        city.setWindSpeed(3.5);

        CityDto result = cityMapper.toDto(city);

        assertNotNull(result);
        assertEquals(8L, result.getId());
        assertEquals("Владивосток", result.getName());
        assertEquals(7.0, result.getTemperature());
    }

    @Test
    void shouldHandleMultipleMappings() {
        City[] cities = {
                createCity(1L, "Москва", 15.5, true),
                createCity(2L, "Казань", 10.0, false),
                createCity(3L, "Сочи", 25.0, true),
                createCity(null, "Новый город", null, null)
        };

        for (City city : cities) {
            CityDto result = cityMapper.toDto(city);
            assertNotNull(result, "Результат не должен быть null для города: " + city.getName());

            assertEquals(city.getId(), result.getId());
            assertEquals(city.getName(), result.getName());
            assertEquals(city.getTemperature(), result.getTemperature());
            assertEquals(city.getFavorite(), result.getFavorite());
        }
    }

    @Test
    void shouldPreserveDoublePrecision() {
        double preciseTemperature = 15.5123456789;
        City city = new City();
        city.setId(9L);
        city.setName("Точный город");
        city.setTemperature(BigDecimal.valueOf(preciseTemperature));

        CityDto result = cityMapper.toDto(city);

        assertEquals(preciseTemperature, result.getTemperature(),
                "Температура должна сохраняться с точностью double");
    }


    private City createCity(Long id, String name, Double temperature, Boolean favorite) {
        City city = new City();
        city.setId(id);
        city.setName(name);
        city.setTemperature(BigDecimal.valueOf(temperature));
        city.setFavorite(favorite);
        city.setTemperatureUpdatedAt(LocalDateTime.now());
        return city;
    }
}
