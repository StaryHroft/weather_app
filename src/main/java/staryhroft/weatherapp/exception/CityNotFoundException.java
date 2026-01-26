package staryhroft.weatherapp.exception;

public class CityNotFoundException extends NotFoundException {

    public CityNotFoundException(String name) {
        super("Город '" + name + "' не найден");
    }
}
