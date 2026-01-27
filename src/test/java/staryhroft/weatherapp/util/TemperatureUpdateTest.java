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
        BigDecimal expectedTemperature = new BigDecimal("25.5");
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);

        when(apiResponse.getTemperature()).thenReturn(expectedTemperature);

        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 15, 14, 30, 0);

        try (MockedStatic<LocalDateTime> mockedDateTime = mockStatic(LocalDateTime.class)) {
            mockedDateTime.when(LocalDateTime::now).thenReturn(fixedTime);

            // When
            TemperatureUpdate.updateCityTemperature(city, apiResponse);

            // Then
            verify(city).setTemperature(expectedTemperature);
            verify(city).setTemperatureUpdatedAt(fixedTime);
            verify(city).setUpdatedAt(fixedTime);
            verify(apiResponse, times(1)).getTemperature();
        }
    }

    @Test
    void updateCityTemperature_ShouldUpdateTemperatureOnly() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);

        BigDecimal temperature = new BigDecimal("18.0");
        when(apiResponse.getTemperature()).thenReturn(temperature);

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        verify(city).setTemperature(temperature);
        verify(city, times(1)).setTemperature((BigDecimal) any());
        verify(city, times(1)).setTemperatureUpdatedAt(any());
        verify(city, times(1)).setUpdatedAt(any());
    }

    //ошибка
    @Test
    void updateCityTemperature_ShouldSetSameTimeForBothTimestamps() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);
        when(apiResponse.getTemperature()).thenReturn(new BigDecimal("20.0"));
        // Фиксируем время для теста
        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 27, 12, 0, 0);

        // Mock статический метод LocalDateTime.now() если используете Mockito 3.4+
        try (MockedStatic<LocalDateTime> mockedDateTime = mockStatic(LocalDateTime.class)) {
            mockedDateTime.when(LocalDateTime::now).thenReturn(fixedTime);

            // Capture the time used
            LocalDateTime[] capturedTimes = new LocalDateTime[2];
            AtomicInteger callIndex = new AtomicInteger(0);

            doAnswer(invocation -> {
                capturedTimes[callIndex.getAndIncrement()] = invocation.getArgument(0);
                return null;
            }).when(city).setTemperatureUpdatedAt(any(LocalDateTime.class));

            doAnswer(invocation -> {
                capturedTimes[callIndex.getAndIncrement()] = invocation.getArgument(0);
                return null;
            }).when(city).setUpdatedAt(any(LocalDateTime.class));

            // When
            TemperatureUpdate.updateCityTemperature(city, apiResponse);

            // Then
            assertEquals(2, callIndex.get());
            assertEquals(fixedTime, capturedTimes[0]);
            assertEquals(fixedTime, capturedTimes[1]);
            verify(city).setTemperature(new BigDecimal("20.0"));
        }
    }


    @Test
    void updateCityTemperature_ShouldHandleNullTemperature() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);

        when(apiResponse.getTemperature()).thenReturn(null);

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        verify(city).setTemperature((BigDecimal) null);
        verify(city).setTemperatureUpdatedAt(any(LocalDateTime.class));
        verify(city).setUpdatedAt(any(LocalDateTime.class));
    }

    @Test
    void updateCityTemperature_ShouldThrowNullPointerExceptionWhenCityIsNull() {
        // Given
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            TemperatureUpdate.updateCityTemperature(null, apiResponse);
        });
    }

    @Test
    void updateCityTemperature_ShouldThrowNullPointerExceptionWhenApiResponseIsNull() {
        // Given
        City city = mock(City.class);

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            TemperatureUpdate.updateCityTemperature(city, null);
        });
    }

    @Test
    void updateCityTemperature_ShouldHandleNegativeTemperature() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);

        BigDecimal negativeTemp = new BigDecimal("-15.5");
        when(apiResponse.getTemperature()).thenReturn(negativeTemp);

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        verify(city).setTemperature(negativeTemp);
    }

    @Test
    void updateCityTemperature_ShouldHandleZeroTemperature() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);

        when(apiResponse.getTemperature()).thenReturn(BigDecimal.ZERO);

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        verify(city).setTemperature(BigDecimal.ZERO);
    }

    @Test
    void updateCityTemperature_ShouldUpdateOnlyRequiredFields() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);
        when(apiResponse.getTemperature()).thenReturn(new BigDecimal("10.0"));

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        // Проверяем, что НЕ вызываются методы, которые не должны вызываться
        verify(city, never()).setName(anyString());
        verify(city, never()).setCreatedAt(any(LocalDateTime.class));
        verify(city, never()).setId(anyLong());

        // Проверяем, что вызываются только нужные методы
        verify(city, times(1)).setTemperature((BigDecimal) any());
        verify(city, times(1)).setTemperatureUpdatedAt(any());
        verify(city, times(1)).setUpdatedAt(any());
    }

    @Test
    void updateCityTemperature_ShouldUseCurrentTime() {
        // Given
        City city = mock(City.class);
        WeatherApiResponse apiResponse = mock(WeatherApiResponse.class);
        when(apiResponse.getTemperature()).thenReturn(new BigDecimal("22.5"));

        LocalDateTime beforeCall = LocalDateTime.now();

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);
        LocalDateTime afterCall = LocalDateTime.now();

        // Then
        verify(city).setTemperatureUpdatedAt(argThat(time ->
                !time.isBefore(beforeCall) && !time.isAfter(afterCall)
        ));

        verify(city).setUpdatedAt(argThat(time ->
                !time.isBefore(beforeCall) && !time.isAfter(afterCall)
        ));
    }

    //ошибка
    @Test
    void constructor_ShouldBePrivate() throws Exception {
        // Given
        Constructor<TemperatureUpdate> constructor =
                TemperatureUpdate.class.getDeclaredConstructor();
        // Then
        assertFalse(constructor.isAccessible());
        assertFalse(Modifier.isPublic(constructor.getModifiers()));
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        // Проверяем, что нельзя создать экземпляр
        constructor.setAccessible(true);
        // При вызове через reflection исключение оборачивается в InvocationTargetException
        InvocationTargetException invocationException = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        // Извлекаем оригинальное исключение
        Throwable actualException = invocationException.getCause();
        assertNotNull(actualException);
        assertInstanceOf(UnsupportedOperationException.class, actualException);

        // Проверяем сообщение
        assertThat(actualException.getMessage())
                .contains("utility class")
                .contains("cannot be instantiated");
    }

    @Test
    void updateCityTemperature_ShouldWorkWithRealObjects() {
        // Интеграционный тест с реальными объектами
        // Given
        City city = new City();
        city.setName("Moscow");
        city.setCreatedAt(LocalDateTime.now().minusDays(1));

        BigDecimal newTemperature = new BigDecimal("30.5");
        WeatherApiResponse apiResponse = new WeatherApiResponse(newTemperature);

        LocalDateTime beforeUpdate = LocalDateTime.now();

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        assertEquals(newTemperature, city.getTemperature());
        assertNotNull(city.getTemperatureUpdatedAt());
        assertNotNull(city.getUpdatedAt());

        // Проверяем, что временные метки установлены
        assertTrue(city.getTemperatureUpdatedAt().isAfter(beforeUpdate) ||
                city.getTemperatureUpdatedAt().equals(beforeUpdate));
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

        BigDecimal preciseTemp = new BigDecimal("22.56789");
        when(apiResponse.getTemperature()).thenReturn(preciseTemp);

        // When
        TemperatureUpdate.updateCityTemperature(city, apiResponse);

        // Then
        verify(city).setTemperature(preciseTemp);
    }
}