package hirjanfabian.ccframework.mapper;

import hirjanfabian.ccframework.dto.CarDTO;
import hirjanfabian.ccframework.entities.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CarModelMapper.class, CarMakeMapper.class})
public interface CarMapper {
    @Mapping(target = "make", ignore = true)
    @Mapping(target = "model", ignore = true)
    Car toCar(CarDTO carDTO);

    CarDTO toCarDTO(Car car);
}