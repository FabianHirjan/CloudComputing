package hirjanfabian.mainserver.service;

import hirjanfabian.mainserver.entity.User;
import hirjanfabian.mainserver.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CarService {
    private static final String CAR_SERVER_URL = "https://firstcarserver-cqd9hgadgke2ewgw.italynorth-01.azurewebsites.net/api/car";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> getCarLocation() {
        User user = getCurrentUser();
        if (user.getCarId() != 1) { // Only carId=1 for firstCarServer
            return ResponseEntity.badRequest().body("User not assigned to this car");
        }
        return restTemplate.getForEntity(CAR_SERVER_URL + "/location", Object.class);
    }

    public ResponseEntity<?> lockCar() {
        User user = getCurrentUser();
        if (user.getCarId() != 1) {
            return ResponseEntity.badRequest().body("User not assigned to this car");
        }
        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
        return restTemplate.exchange(CAR_SERVER_URL + "/lock", HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<?> unlockCar() {
        User user = getCurrentUser();
        if (user.getCarId() != 1) {
            return ResponseEntity.badRequest().body("User not assigned to this car");
        }
        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
        return restTemplate.exchange(CAR_SERVER_URL + "/unlock", HttpMethod.POST, entity, String.class);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}