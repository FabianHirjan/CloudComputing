package hirjanfabian.mainserver.controller;

import hirjanfabian.mainserver.Entity.CarServer;
import hirjanfabian.mainserver.Repository.CarServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/car")
public class CarController {

    @Autowired
    private CarServerRepository carServerRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Endpoint to add a new car server URL
    @PostMapping("/servers")
    public ResponseEntity<String> addCarServer(@RequestBody CarServer carServer) {
        try {
            carServerRepository.save(carServer);
            return ResponseEntity.ok("Car server added: " + carServer.getUrl());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding car server: " + e.getMessage());
        }
    }

    // Proxy endpoint for /api/car/data/{carId}
    @GetMapping("/data/{carId}")
    public ResponseEntity<?> getCarData(@PathVariable Integer carId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        CarServer carServer = carServerRepository.findByCarId(carId).orElse(null);
        if (carServer == null) {
            return ResponseEntity.status(404).body("Car server not found for car ID: " + carId);
        }

        String url = carServer.getUrl() + "/api/car/data";
        try {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching car data: " + e.getMessage());
        }
    }

    // Proxy endpoint for /api/car/lock/{carId}
    @PostMapping("/lock/{carId}")
    public ResponseEntity<?> lockCar(@PathVariable Integer carId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return proxyPostRequest(carId, "/api/car/lock", authHeader);
    }

    // Proxy endpoint for /api/car/unlock/{carId}
    @PostMapping("/unlock/{carId}")
    public ResponseEntity<?> unlockCar(@PathVariable Integer carId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return proxyPostRequest(carId, "/api/car/unlock", authHeader);
    }

    // Proxy endpoint for /api/car/start/{carId}
    @PostMapping("/start/{carId}")
    public ResponseEntity<?> startCar(@PathVariable Integer carId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return proxyPostRequest(carId, "/api/car/start", authHeader);
    }

    // Proxy endpoint for /api/car/stop/{carId}
    @PostMapping("/stop/{carId}")
    public ResponseEntity<?> stopCar(@PathVariable Integer carId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return proxyPostRequest(carId, "/api/car/stop", authHeader);
    }

    // Proxy endpoint for /api/car/checkunrent/{carId}
    @GetMapping("/checkunrent/{carId}")
    public ResponseEntity<?> checkUnrent(@PathVariable Integer carId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        CarServer carServer = carServerRepository.findByCarId(carId).orElse(null);
        if (carServer == null) {
            return ResponseEntity.status(404).body("Car server not found for car ID: " + carId);
        }

        String url = carServer.getUrl() + "/api/car/checkunrent";
        try {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error checking unrent status: " + e.getMessage());
        }
    }

    // Proxy endpoint for /api/car/location/{carId}
    @GetMapping("/location/{carId}")
    public ResponseEntity<?> getLocation(@PathVariable Integer carId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        CarServer carServer = carServerRepository.findByCarId(carId).orElse(null);
        if (carServer == null) {
            return ResponseEntity.status(404).body("Car server not found for car ID: " + carId);
        }

        String url = carServer.getUrl() + "/api/car/location";
        try {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching location: " + e.getMessage());
        }
    }

    // Proxy endpoint for /api/car/details/{carId}
    @GetMapping("/details/{carId}")
    public ResponseEntity<?> getDetails(@PathVariable Integer carId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        CarServer carServer = carServerRepository.findByCarId(carId).orElse(null);
        if (carServer == null) {
            return ResponseEntity.status(404).body("Car server not found for car ID: " + carId);
        }

        String url = carServer.getUrl() + "/api/car/details";
        try {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching details: " + e.getMessage());
        }
    }

    // Helper method for POST requests
    private ResponseEntity<?> proxyPostRequest(Integer carId, String endpoint, String authHeader) {
        CarServer carServer = carServerRepository.findByCarId(carId).orElse(null);
        if (carServer == null) {
            return ResponseEntity.status(404).body("Car server not found for car ID: " + carId);
        }

        String url = carServer.getUrl() + endpoint;
        try {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
        }
    }
}