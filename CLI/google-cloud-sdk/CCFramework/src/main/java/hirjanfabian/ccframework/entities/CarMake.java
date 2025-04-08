package hirjanfabian.ccframework.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="car_makes")
@Getter
@Setter
public class CarMake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "make", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CarModel> models;

}