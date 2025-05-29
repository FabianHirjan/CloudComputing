package hirjanfabian.mainserver.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "car_servers")
public class CarServer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "car_id", nullable = false, unique = true)
    private Integer carId;

    @Column(name = "url", nullable = false)
    private String url;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}