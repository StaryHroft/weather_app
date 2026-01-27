package staryhroft.weatherapp.mapper;

import org.springframework.stereotype.Component;
import staryhroft.weatherapp.dto.response.CityDto;
import staryhroft.weatherapp.entity.City;

@Component
public class CityMapper {

    public CityDto toDto(City city) {
        if (city == null) {
            return null;
        }
        CityDto cityDto = new CityDto();

        cityDto.setId(city.getId());
        cityDto.setName(city.getName());
        cityDto.setTemperature(city.getTemperature());
        cityDto.setFavorite(city.getFavorite());
        cityDto.setTemperatureUpdatedAt(city.getTemperatureUpdatedAt());

        return cityDto;
    }
}
