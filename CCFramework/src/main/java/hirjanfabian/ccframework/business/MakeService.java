package hirjanfabian.ccframework.business;

import hirjanfabian.ccframework.dto.CarMakeDTO;
import hirjanfabian.ccframework.entities.CarMake;
import hirjanfabian.ccframework.mapper.CarMakeMapper;
import hirjanfabian.ccframework.repositories.CarMakeRepository;
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
public class MakeService {
    private static final Logger log = LoggerFactory.getLogger(MakeService.class);

    private final CarMakeRepository carMakeRepository;
    private final CarMakeMapper carMakeMapper;

    public List<CarMakeDTO> getAllCarMakes(Pageable pageable) {
        log.debug("Fetching all car makes with pagination: {}", pageable);
        List<CarMake> carMakes = carMakeRepository.findAll(pageable).getContent();
        return carMakes.stream()
                .map(carMakeMapper::toCarMakeDTO)
                .collect(Collectors.toList());
    }

    public Optional<CarMakeDTO> getCarMakeById(@Min(1) int id) {
        log.debug("Fetching car make with ID: {}", id);
        Optional<CarMake> carMake = carMakeRepository.findById(id);
        if (carMake.isEmpty()) {
            log.debug("No car make found for ID: {}", id);
        }
        return carMake.map(carMakeMapper::toCarMakeDTO);
    }

    public CarMakeDTO saveCarMake(@Valid @NotNull CarMakeDTO carMakeDTO) {
        log.debug("Saving car make: {}", carMakeDTO);
        CarMake carMake = carMakeMapper.toCarMake(carMakeDTO);
        CarMake savedCarMake = carMakeRepository.save(carMake);
        log.info("Successfully saved car make with ID: {}", savedCarMake.getId());
        return carMakeMapper.toCarMakeDTO(savedCarMake);
    }

    public CarMakeDTO updateCarMake(@Min(1) int id, @Valid @NotNull CarMakeDTO carMakeDTO) {
        log.debug("Updating car make with ID: {}", id);
        CarMake existingMake = carMakeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("CarMake with ID {} not found", id);
                    return new IllegalArgumentException("CarMake with ID " + id + " not found");
                });
        CarMake updatedMake = carMakeMapper.toCarMake(carMakeDTO);
        updatedMake.setId(id);
        CarMake savedCarMake = carMakeRepository.save(updatedMake);
        log.info("Successfully updated car make with ID: {}", savedCarMake.getId());
        return carMakeMapper.toCarMakeDTO(savedCarMake);
    }

    public void deleteCarMakeById(@Min(1) int id) {
        log.debug("Attempting to delete car make with ID: {}", id);
        if (!carMakeRepository.existsById(id)) {
            log.error("No car make found to delete for ID: {}", id);
            throw new IllegalArgumentException("CarMake with ID " + id + " not found");
        }
        carMakeRepository.deleteById(id);
        log.info("Successfully deleted car make with ID: {}", id);
    }
}