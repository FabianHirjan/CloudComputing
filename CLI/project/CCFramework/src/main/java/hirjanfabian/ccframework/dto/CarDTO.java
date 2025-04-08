package hirjanfabian.ccframework.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarDTO {
    private int id;

    @NotBlank(message = "VIN cannot be blank")
    private String vin;

    @JsonIgnore
    private CarMakeDTO make;

    @NotNull(message = "Model cannot be null")
    private CarModelDTO model;

    @NotBlank(message = "Color cannot be blank")
    private String color;

    @Min(value = 1980, message = "Year must be greater than or equal to 1980")
    @Max(value = 2025, message = "Year must be less than or equal to 2025")
    private int year;

    @NotBlank(message = "License plate cannot be blank")
    private String licensePlate;

    @NotBlank(message = "Fuel type cannot be blank")
    private String fuelType;
}