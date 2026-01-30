package staryhroft.weatherapp.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import staryhroft.weatherapp.entity.City;
import staryhroft.weatherapp.service.WeatherApiResponse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemperatureUpdateTest {

    @Test
    void updateCityTemperature_ShouldUpdateAllFieldsCorrectly() {
        // Given
        City city = mock(City.class);
        Double expectedTemperature = 25.5;
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);

        when(apiResponse.getTemperature()).thenReturn(expectedTemperature);

        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 15, 14, 30, 0);

        try (MockedStatic<LocalDateTime> mockedDateTime = mockStatic(LocalDateTime.class)) {
            mockedDateTime.when(LocalDateTime::now).thenReturn(fixedTime);

            // When
            TemperatureUpdate.updateCityTemperature(city, apiResponse);

            // Then
            verify(city).setTemperature(expectedTemperature);
            verify(city).setUpdatedAt(fixedTime);
            verify(city).setUpdatedAt(fixedTime);
            verify(apiResponse, times(1)).getTemperature();
        }
    }

    @Test
    void updateCityTemperature_ShouldUpdateTemperatureOnly() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);

        Double temperature = 18.0;
        when(apiResponse.getTemperature()).thenReturn(temperature);

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        verify(city).setTemperature(temperature);
        verify(city, times(1)).setTemperature(any());
        verify(city, times(1)).setUpdatedAt(any());
    }

    @Test
    void updateCityTemperature_ShouldUpdateOnlyRequiredFields() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);
        when(apiResponse.getTemperature()).thenReturn(10.0);

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        // Проверяем, что НЕ вызываются методы, которые не должны вызываться
        verify(city, never()).setName(anyString());
        verify(city, never()).setCreatedAt(any(LocalDateTime.class));
        verify(city, never()).setId(anyLong());

        // Проверяем, что вызываются только нужные методы
        verify(city, times(1)).setTemperature(any());
        verify(city, times(1)).setUpdatedAt(any());
        verify(city, times(1)).setUpdatedAt(any());
    }

    @Test
    void updateCityTemperature_ShouldUseCurrentTime() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);
        when(apiResponse.getTemperature()).thenReturn(22.5);

        LocalDateTime beforeCall = LocalDateTime.now();

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);
        LocalDateTime afterCall = LocalDateTime.now();

        // Then
        verify(city).setUpdatedAt(argThat(time ->
                !time.isBefore(beforeCall) && !time.isAfter(afterCall)
        ));
    }

    @Test
    void updateCityTemperature_ShouldWorkWithRealObjects() {
        // Интеграционный тест с реальными объектами
        // Given
        City city = new City();
        city.setName("Moscow");
        city.setCreatedAt(LocalDateTime.now().minusDays(1));

        Double newTemperature = 30.5;
        WeatherApiResponse apiResponse = new WeatherApiResponse(newTemperature);

        LocalDateTime beforeUpdate = LocalDateTime.now();

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        assertEquals(newTemperature, city.getTemperature());
        assertNotNull(city.getUpdatedAt());

        // Проверяем, что временные метки установлены
        assertTrue(city.getUpdatedAt().isAfter(beforeUpdate) ||
                city.getUpdatedAt().equals(beforeUpdate));

        // Проверяем, что другие поля не изменились
        assertEquals("Moscow", city.getName());
        assertNotNull(city.getCreatedAt());
    }

    @Test
    void updateCityTemperature_ShouldHandleHighPrecisionTemperature() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);

        Double preciseTemp = 22.56789;
        when(apiResponse.getTemperature()).thenReturn(preciseTemp);

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        verify(city).setTemperature(preciseTemp);
    }
}