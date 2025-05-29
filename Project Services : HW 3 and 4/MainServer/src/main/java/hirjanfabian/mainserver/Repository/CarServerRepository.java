package hirjanfabian.mainserver.Repository;

import hirjanfabian.mainserver.Entity.CarServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarServerRepository extends JpaRepository<CarServer, Integer> {
    Optional<CarServer> findByCarId(Integer carId);
}