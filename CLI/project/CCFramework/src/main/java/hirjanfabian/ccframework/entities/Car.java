package hirjanfabian.ccframework.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cars")
@Getter
@Setter
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "vin", nullable = false)
    private String vin;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "make_id", nullable = false)
    private CarMake make;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel model;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(name = "fuel_type", nullable = false)
    private String fuelType;
}