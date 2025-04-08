package hirjanfabian.ccframework.controllers;

import hirjanfabian.ccframework.business.ModelService;
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
@RequestMapping("/api/car/model")
@RequiredArgsConstructor
public class ModelController {
    private static final Logger log = LoggerFactory.getLogger(ModelController.class);

    private final ModelService modelService;

    @GetMapping
    public ResponseEntity<List<CarModelDTO>> getAllCarModels(Pageable pageable) {
        log.debug("Fetching all car models with pagination: {}", pageable);
        List<CarModelDTO> models = modelService.getAllCarModels(pageable);
        return ResponseEntity.ok(models);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarModelDTO> getCarModelById(@PathVariable int id) {
        log.debug("Fetching car model with ID: {}", id);
        return modelService.getCarModelById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.debug("No car model found for ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<CarModelDTO> createCarModel(@Valid @RequestBody CarModelDTO carModelDTO) {
        log.debug("Received request to create car model: {}", carModelDTO);
        CarModelDTO savedModel = modelService.saveCarModel(carModelDTO);
        log.info("Car model created successfully with ID: {}", savedModel.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarModelDTO> updateCarModel(
            @PathVariable int id,
            @Valid @RequestBody CarModelDTO carModelDTO) {
        log.debug("Received request to update car model with ID: {}", id);
        CarModelDTO updatedModel = modelService.updateCarModel(id, carModelDTO);
        log.info("Car model updated successfully with ID: {}", updatedModel.getId());
        return ResponseEntity.ok(updatedModel);
    }

    // Delete model
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarModel(@PathVariable int id) {
        log.debug("Received request to delete car model with ID: {}", id);
        modelService.deleteCarModelById(id);
        log.info("Car model deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
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