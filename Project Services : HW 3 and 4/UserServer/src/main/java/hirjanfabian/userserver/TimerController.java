package hirjanfabian.userserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/timer")
public class TimerController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/start/{username}")
    public ResponseEntity<String> startTimer(@PathVariable String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        user.setStartDate(LocalDateTime.now());
        user.setEndDate(null);
        user.setDuration(null);
        userRepository.save(user);

        return ResponseEntity.ok("Timer started at " + user.getStartDate());
    }

    @PostMapping("/stop/{username}")
    public ResponseEntity<String> stopTimer(@PathVariable String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        if (user.getStartDate() == null) {
            return ResponseEntity.badRequest().body("Timer has not been started.");
        }

        LocalDateTime endTime = LocalDateTime.now();
        user.setEndDate(endTime);
        Duration duration = Duration.between(user.getStartDate(), endTime);
        user.setDuration(duration);
        userRepository.save(user);

        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;

        return ResponseEntity.ok("Timer stopped at " + endTime + ". Duration: " + minutes + " minutes, " + seconds + " seconds.");
    }
}

