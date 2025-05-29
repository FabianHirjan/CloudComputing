package hirjanfabian.carserver3.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Car {
    private int id = 1;
    private int ownerId = 1;
        private String make = "Testla";
    private String model = "Model 3";
    private String year = "2025";
    private String VIN = "1HGBH41JXMN109186";
    private double lastLongitude = 47.1600;
    private double lastLatitude = 27.5900;
    private int estimatedRange = 500;
    private int kilometersDriven = 15000;
    private Boolean areLightsOn = false;
    private Boolean isEngineOn = false;
    private Boolean isCarLocked = true;
}