package hirjanfabian.ccframework.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarModelDTO {
    private int id;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Make cannot be null")
    private CarMakeDTO make;
}