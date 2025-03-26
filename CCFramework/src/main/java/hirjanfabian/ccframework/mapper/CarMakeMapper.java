package hirjanfabian.ccframework.mapper;

import hirjanfabian.ccframework.dto.CarMakeDTO;
import hirjanfabian.ccframework.entities.CarMake;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMakeMapper {
    @Mapping(target = "models", ignore = true)
    CarMakeDTO toCarMakeDTO(CarMake carMake);

    @Mapping(target = "models", ignore = true)
    CarMake toCarMake(CarMakeDTO carMakeDTO);
}