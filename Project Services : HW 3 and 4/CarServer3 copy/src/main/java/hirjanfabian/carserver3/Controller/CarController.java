package hirjanfabian.carserver3.Controller;

import hirjanfabian.carserver3.Entity.Car;
import hirjanfabian.carserver3.Service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/car")
public class CarController {

    @Autowired
    private CarService carService;

    // Endpoint to get car details (make, model, location, lock state, lights, engine)
    @GetMapping("/details")
    public ResponseEntity<CarDetailsDTO> getCarDetails() {
        Car car = carService.getCar();
        CarDetailsDTO details = new CarDetailsDTO(
                car.getMake(),
                car.getModel(),
                car.getLastLongitude(),
                car.getLastLatitude(),
                car.getIsCarLocked(),
                car.getAreLightsOn(),
                car.getIsEngineOn()
        );
        return ResponseEntity.ok(details);
    }

    // Endpoint to check if car is unrented (lights off, engine off, locked)
    @GetMapping("/check-unrent")
    public ResponseEntity<Map<String, Object>> checkUnrent() {
        Car car = carService.getCar();
        boolean isUnrented = carService.isUnrented();
        Map<String, Object> response = new HashMap<>();

        if (isUnrented) {
            response.put("status", "success");
            response.put("message", "Car is unrented");
            return ResponseEntity.ok(response);
        } else {
            List<String> issues = new ArrayList<>();
            if (!car.getIsCarLocked()) {
                issues.add("Car is unlocked");
            }
            if (car.getAreLightsOn()) {
                issues.add("Lights are on");
            }
            if (car.getIsEngineOn()) {
                issues.add("Engine is on");
            }
            response.put("status", "error");
            response.put("message", "Car is not unrented");
            response.put("issues", issues);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Endpoint to lock the car
    @PostMapping("/lock")
    public ResponseEntity<Map<String, String>> lockCar() {
        carService.lockCar();
        Map<String, String> response = Map.of("message", "Car locked successfully");
        return ResponseEntity.ok(response);
    }

    // Endpoint to unlock the car
    @PostMapping("/unlock")
    public ResponseEntity<Map<String, String>> unlockCar() {
        carService.unlockCar();
        Map<String, String> response = Map.of("message", "Car unlocked successfully");
        return ResponseEntity.ok(response);
    }

    // DTO for car details
    public record CarDetailsDTO(
            String make,
            String model,
            double lastLongitude,
            double lastLatitude,
            boolean locked,
            boolean lightsOn,
            boolean engineOn
    ) {}
}