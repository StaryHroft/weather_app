package staryhroft.weatherapp.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import staryhroft.weatherapp.entity.City;
import staryhroft.weatherapp.repository.CityRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты с реальной базой данных
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CityRestControllerV1IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;
//ошибка
    @Test
    void integrationTest_GetAllCities() throws Exception {
        // Given: создаем тестовые данные в БД
        City city1 = City.builder()
                .name("Moscow")
                .temperature(BigDecimal.valueOf(15.5))
                .favorite(true)
                .build();

        City city2 = City.builder()
                .name("London")
                .temperature(BigDecimal.valueOf(12.0))
                .favorite(false)
                .build();

        cityRepository.saveAll(List.of(city1, city2));

        // When & Then
        mockMvc.perform(get("/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Moscow"))
                .andExpect(jsonPath("$[0].favorite").value(true))
                .andExpect(jsonPath("$[1].name").value("London"))
                .andExpect(jsonPath("$[1].favorite").value(false));
    }
}