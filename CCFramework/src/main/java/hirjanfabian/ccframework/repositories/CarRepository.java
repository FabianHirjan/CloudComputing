package hirjanfabian.ccframework.repositories;

import hirjanfabian.ccframework.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {
}
