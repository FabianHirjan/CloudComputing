package hirjanfabian.ccframework.controllers;

import hirjanfabian.ccframework.business.MakeService;
import hirjanfabian.ccframework.dto.CarMakeDTO;
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
@RequestMapping("/api/car/make")
@RequiredArgsConstructor
public class MakeController {
    private static final Logger log = LoggerFactory.getLogger(MakeController.class);

    private final MakeService makeService;

    @GetMapping
    public ResponseEntity<List<CarMakeDTO>> getAllCarMakes(Pageable pageable) {
        log.debug("Fetching all car makes with pagination: {}", pageable);
        List<CarMakeDTO> makes = makeService.getAllCarMakes(pageable);
        return ResponseEntity.ok(makes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarMakeDTO> getCarMakeById(@PathVariable int id) {
        log.debug("Fetching car make with ID: {}", id);
        return makeService.getCarMakeById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.debug("No car make found for ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<CarMakeDTO> createCarMake(@Valid @RequestBody CarMakeDTO carMakeDTO) {
        log.debug("Received request to create car make: {}", carMakeDTO);
        CarMakeDTO savedMake = makeService.saveCarMake(carMakeDTO);
        log.info("Car make created successfully with ID: {}", savedMake.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMake);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarMakeDTO> updateCarMake(
            @PathVariable int id,
            @Valid @RequestBody CarMakeDTO carMakeDTO) {
        log.debug("Received request to update car make with ID: {}", id);
        CarMakeDTO updatedMake = makeService.updateCarMake(id, carMakeDTO);
        log.info("Car make updated successfully with ID: {}", updatedMake.getId());
        return ResponseEntity.ok(updatedMake);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarMake(@PathVariable int id) {
        log.debug("Received request to delete car make with ID: {}", id);
        makeService.deleteCarMakeById(id);
        log.info("Car make deleted successfully with ID: {}", id);
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