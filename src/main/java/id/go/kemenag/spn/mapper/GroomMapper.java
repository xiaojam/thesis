package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.groom.request.GroomCreateRequest;
import id.go.kemenag.spn.entity.marriage.Groom;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface GroomMapper {

    Groom convert(GroomCreateRequest source);
}
