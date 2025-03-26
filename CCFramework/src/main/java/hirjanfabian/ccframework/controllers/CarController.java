package hirjanfabian.ccframework.controllers;

import hirjanfabian.ccframework.business.CarService;
import hirjanfabian.ccframework.dto.CarDTO;
import hirjanfabian.ccframework.dto.CarMakeDTO;
import hirjanfabian.ccframework.dto.CarModelDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private static final Logger log = LoggerFactory.getLogger(CarController.class);

    private final CarService carService;

    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars(Pageable pageable) {
        log.debug("Fetching all cars with pagination: {}", pageable);
        List<CarDTO> cars = carService.getAllCars(pageable);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable int id) {
        log.debug("Fetching car with ID: {}", id);
        return carService.getCarById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.debug("No car found for ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<CarDTO> createCar(@Valid @RequestBody CarDTO carDTO) {
        log.debug("Received request to create car: {}", carDTO);
        CarDTO savedCar = carService.saveCar(carDTO);
        log.info("Car created successfully with ID: {}", savedCar.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(
            @PathVariable int id,
            @Valid @RequestBody CarDTO carDTO) {
        log.debug("Received request to update car with ID: {}", id);
        CarDTO updatedCar = carService.updateCar(id, carDTO);
        log.info("Car updated successfully with ID: {}", updatedCar.getId());
        return ResponseEntity.ok(updatedCar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable int id) {
        log.debug("Received request to delete car with ID: {}", id);
        carService.deleteCarById(id);
        log.info("Car deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/make")
    public ResponseEntity<CarMakeDTO> getCarMake(@PathVariable int id) {
        log.debug("Fetching make for car with ID: {}", id);
        CarMakeDTO make = carService.getCarMakeById(id);
        return ResponseEntity.ok(make);
    }

    @GetMapping("/{id}/model")
    public ResponseEntity<CarModelDTO> getCarModel(@PathVariable int id) {
        log.debug("Fetching model for car with ID: {}", id);
        CarModelDTO model = carService.getCarModelById(id);
        return ResponseEntity.ok(model);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}