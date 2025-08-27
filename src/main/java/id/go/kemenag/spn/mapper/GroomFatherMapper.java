package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.groom.request.GroomFatherCreateRequest;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface GroomFatherMapper {

    GroomFather convert(GroomFatherCreateRequest source);
}
