package hirjanfabian.ccframework.repositories;

import hirjanfabian.ccframework.entities.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarModelRepository extends JpaRepository<CarModel, Integer> {
}
