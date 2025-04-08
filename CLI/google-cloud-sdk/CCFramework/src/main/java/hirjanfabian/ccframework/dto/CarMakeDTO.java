package hirjanfabian.ccframework.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CarMakeDTO {
    @NotNull
    private int id;
    private String name;

    @JsonIgnore
    private List<CarModelDTO> models;


}