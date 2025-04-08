package hirjanfabian.ccframework.business;

import hirjanfabian.ccframework.dto.CarModelDTO;
import hirjanfabian.ccframework.entities.CarMake;
import hirjanfabian.ccframework.entities.CarModel;
import hirjanfabian.ccframework.mapper.CarModelMapper;
import hirjanfabian.ccframework.repositories.CarMakeRepository;
import hirjanfabian.ccframework.repositories.CarModelRepository;
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
public class ModelService {
    private static final Logger log = LoggerFactory.getLogger(ModelService.class);

    private final CarModelRepository carModelRepository;
    private final CarMakeRepository carMakeRepository;
    private final CarModelMapper carModelMapper;

    public List<CarModelDTO> getAllCarModels(Pageable pageable) {
        log.debug("Fetching all car models with pagination: {}", pageable);
        List<CarModel> carModels = carModelRepository.findAll(pageable).getContent();
        return carModels.stream()
                .map(carModelMapper::toCarModelDTO)
                .collect(Collectors.toList());
    }

    public Optional<CarModelDTO> getCarModelById(@Min(1) int id) {
        log.debug("Fetching car model with ID: {}", id);
        Optional<CarModel> carModel = carModelRepository.findById(id);
        if (carModel.isEmpty()) {
            log.debug("No car model found for ID: {}", id);
        }
        return carModel.map(carModelMapper::toCarModelDTO);
    }

    public CarModelDTO saveCarModel(@Valid @NotNull CarModelDTO carModelDTO) {
        log.debug("Saving car model: {}", carModelDTO);

        if (carModelDTO.getMake() == null || carModelDTO.getMake().getId() <= 0) {
            log.error("CarMake ID must be provided and valid");
            throw new IllegalArgumentException("CarMake ID must be provided and valid");
        }

        CarMake carMake = carMakeRepository.findById(carModelDTO.getMake().getId())
                .orElseThrow(() -> {
                    log.error("CarMake with ID {} not found", carModelDTO.getMake().getId());
                    return new IllegalArgumentException("CarMake with ID " + carModelDTO.getMake().getId() + " not found");
                });

        CarModel carModel = carModelMapper.toCarModel(carModelDTO);
        carModel.setMake(carMake);

        CarModel savedCarModel = carModelRepository.save(carModel);
        log.info("Successfully saved car model with ID: {}", savedCarModel.getId());
        return carModelMapper.toCarModelDTO(savedCarModel);
    }

    public CarModelDTO updateCarModel(@Min(1) int id, @Valid @NotNull CarModelDTO carModelDTO) {
        log.debug("Updating car model with ID: {}", id);

        CarModel existingModel = carModelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("CarModel with ID {} not found", id);
                    return new IllegalArgumentException("CarModel with ID " + id + " not found");
                });

        if (carModelDTO.getMake() == null || carModelDTO.getMake().getId() <= 0) {
            log.error("CarMake ID must be provided and valid");
            throw new IllegalArgumentException("CarMake ID must be provided and valid");
        }

        CarMake carMake = carMakeRepository.findById(carModelDTO.getMake().getId())
                .orElseThrow(() -> {
                    log.error("CarMake with ID {} not found", carModelDTO.getMake().getId());
                    return new IllegalArgumentException("CarMake with ID " + carModelDTO.getMake().getId() + " not found");
                });

        CarModel updatedModel = carModelMapper.toCarModel(carModelDTO);
        updatedModel.setId(id);
        updatedModel.setMake(carMake);

        CarModel savedCarModel = carModelRepository.save(updatedModel);
        log.info("Successfully updated car model with ID: {}", savedCarModel.getId());
        return carModelMapper.toCarModelDTO(savedCarModel);
    }

    public void deleteCarModelById(@Min(1) int id) {
        log.debug("Attempting to delete car model with ID: {}", id);
        if (!carModelRepository.existsById(id)) {
            log.error("No car model found to delete for ID: {}", id);
            throw new IllegalArgumentException("CarModel with ID " + id + " not found");
        }
        carModelRepository.deleteById(id);
        log.info("Successfully deleted car model with ID: {}", id);
    }
}