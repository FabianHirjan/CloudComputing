package hirjanfabian.ccframework.business;

import hirjanfabian.ccframework.dto.CarDTO;
import hirjanfabian.ccframework.dto.CarMakeDTO;
import hirjanfabian.ccframework.dto.CarModelDTO;
import hirjanfabian.ccframework.entities.Car;
import hirjanfabian.ccframework.entities.CarModel;
import hirjanfabian.ccframework.mapper.CarMakeMapper;
import hirjanfabian.ccframework.mapper.CarMapper;
import hirjanfabian.ccframework.mapper.CarModelMapper;
import hirjanfabian.ccframework.repositories.CarModelRepository;
import hirjanfabian.ccframework.repositories.CarRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {
    private static final Logger log = LoggerFactory.getLogger(CarService.class);

    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final CarMapper carMapper;
    private final CarMakeMapper carMakeMapper;
    private final CarModelMapper carModelMapper;

    // Read all cars
    public List<CarDTO> getAllCars(Pageable pageable) {
        log.debug("Fetching all cars with pagination: {}", pageable);
        List<Car> cars = carRepository.findAll(pageable).getContent();
        return cars.stream()
                .map(carMapper::toCarDTO)
                .collect(Collectors.toList());
    }

    // Read one car
    public Optional<CarDTO> getCarById(@Min(1) int id) {
        log.debug("Fetching car with ID: {}", id);
        Optional<Car> car = carRepository.findById(id);
        if (car.isEmpty()) {
            log.debug("No car found for ID: {}", id);
        }
        return car.map(carMapper::toCarDTO);
    }

    // Create car
    public CarDTO saveCar(@Valid @NotNull CarDTO carDTO) {
        log.debug("Saving car: {}", carDTO);

        if (carDTO.getModel() == null || carDTO.getModel().getId() <= 0) {
            log.error("Model ID must be provided and valid");
            throw new IllegalArgumentException("Model ID must be provided and valid");
        }

        CarModel carModel = carModelRepository.findById(carDTO.getModel().getId())
                .orElseThrow(() -> {
                    log.error("CarModel with ID {} not found", carDTO.getModel().getId());
                    return new IllegalArgumentException("CarModel with ID " + carDTO.getModel().getId() + " not found");
                });

        Car car = carMapper.toCar(carDTO);
        car.setModel(carModel);
        car.setMake(carModel.getMake());

        Car savedCar = carRepository.save(car);
        log.info("Successfully saved car with ID: {}", savedCar.getId());
        return carMapper.toCarDTO(savedCar);
    }

    // Update car
    public CarDTO updateCar(@Min(1) int id, @Valid @NotNull CarDTO carDTO) {
        log.debug("Updating car with ID: {}", id);

        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Car with ID {} not found", id);
                    return new IllegalArgumentException("Car with ID " + id + " not found");
                });

        if (carDTO.getModel() == null || carDTO.getModel().getId() <= 0) {
            log.error("Model ID must be provided and valid");
            throw new IllegalArgumentException("Model ID must be provided and valid");
        }

        CarModel carModel = carModelRepository.findById(carDTO.getModel().getId())
                .orElseThrow(() -> {
                    log.error("CarModel with ID {} not found", carDTO.getModel().getId());
                    return new IllegalArgumentException("CarModel with ID " + carDTO.getModel().getId() + " not found");
                });

        Car updatedCar = carMapper.toCar(carDTO);
        updatedCar.setId(id);
        updatedCar.setModel(carModel);
        updatedCar.setMake(carModel.getMake());

        Car savedCar = carRepository.save(updatedCar);
        log.info("Successfully updated car with ID: {}", savedCar.getId());
        return carMapper.toCarDTO(savedCar);
    }

    // Delete car
    public void deleteCarById(@Min(1) int id) {
        log.debug("Attempting to delete car with ID: {}", id);
        if (!carRepository.existsById(id)) {
            log.error("No car found to delete for ID: {}", id);
            throw new IllegalArgumentException("Car with ID " + id + " not found");
        }
        carRepository.deleteById(id);
        log.info("Successfully deleted car with ID: {}", id);
    }

    // Get make for a car
    public CarMakeDTO getCarMakeById(@Min(1) int id) {
        log.debug("Fetching make for car with ID: {}", id);
        Car car = carRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Car with ID {} not found", id);
                    return new IllegalArgumentException("Car with ID " + id + " not found");
                });
        return carMakeMapper.toCarMakeDTO(car.getMake());
    }

    // Get model for a car
    public CarModelDTO getCarModelById(@Min(1) int id) {
        log.debug("Fetching model for car with ID: {}", id);
        Car car = carRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Car with ID {} not found", id);
                    return new IllegalArgumentException("Car with ID " + id + " not found");
                });
        return carModelMapper.toCarModelDTO(car.getModel());
    }
}