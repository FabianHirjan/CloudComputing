package hirjanfabian.userserver;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nume;
    private String prenume;
    private String rol;

    private LocalDate dob;
    private String phoneNr;
    private String email;

    private String carId;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Duration duration;

    @Column(unique = true)
    private String username;

    private String password;
}

