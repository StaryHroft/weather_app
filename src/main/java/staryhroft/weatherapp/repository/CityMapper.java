//package staryhroft.weatherapp.repository;
//
//import org.jspecify.annotations.Nullable;
//import org.springframework.jdbc.core.RowMapper;
//import staryhroft.weatherapp.model.City;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class CityMapper implements RowMapper<City> {
//    @Override
//    public @Nullable City mapRow(ResultSet resultSet, int i) throws SQLException {
//        City city = new City();
//        city.setId(resultSet.getInt("id"));
//        city.setName(resultSet.getString("name"));
//        city.setTemp(resultSet.getFloat("temp"));
//        return city;
//    }
//}
