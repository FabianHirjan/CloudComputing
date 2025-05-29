package hirjanfabian.carserver3.Service;

import hirjanfabian.carserver3.Entity.Car;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class CarService {
    private Car car; // Removed 'final' to allow multiple assignments
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String filePath = "car.json";

    public CarService() {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                car = objectMapper.readValue(file, Car.class);
            } catch (IOException e) {
                car = new Car();
            }
        } else {
            car = new Car();
        }
    }

    public Car getCar() {
        return car;
    }

    public boolean isUnrented() {
        return !car.getAreLightsOn() && !car.getIsEngineOn() && car.getIsCarLocked();
    }

    public void lockCar() {
        car.setIsCarLocked(true);
        saveToFile();
    }

    public void unlockCar() {
        car.setIsCarLocked(false);
        saveToFile();
    }

    private void saveToFile() {
        try {
            objectMapper.writeValue(new File(filePath), car);
        } catch (IOException e) {
            e.printStackTrace(); // Handle error appropriately in production
        }
    }
}