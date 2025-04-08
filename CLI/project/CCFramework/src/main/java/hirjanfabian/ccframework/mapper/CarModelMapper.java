package hirjanfabian.ccframework.mapper;

import hirjanfabian.ccframework.dto.CarModelDTO;
import hirjanfabian.ccframework.entities.CarModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CarMakeMapper.class})
public interface CarModelMapper {
    CarModelDTO toCarModelDTO(CarModel carModel);
    CarModel toCarModel(CarModelDTO carModelDTO);
}