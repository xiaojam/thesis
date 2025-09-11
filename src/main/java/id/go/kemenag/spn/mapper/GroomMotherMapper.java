package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.groom.request.GroomFatherUpdateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomMotherCreateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomMotherUpdateRequest;
import id.go.kemenag.spn.dto.groom.response.GroomMotherResponse;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import id.go.kemenag.spn.entity.marriage.GroomMother;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface GroomMotherMapper {

    GroomMother convert(GroomMotherCreateRequest source);

    GroomMotherResponse convert(GroomMother source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(GroomMotherUpdateRequest source, @MappingTarget GroomMother target);
}
