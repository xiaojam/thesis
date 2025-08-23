package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.groom.request.GroomMotherCreateRequest;
import id.go.kemenag.spn.entity.marriage.GroomMother;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface GroomMotherMapper {

    GroomMother convert(GroomMotherCreateRequest source);
}
