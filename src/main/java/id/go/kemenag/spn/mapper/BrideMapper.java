package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.bride.request.BrideCreateRequest;
import id.go.kemenag.spn.dto.bride.response.BrideResponse;
import id.go.kemenag.spn.entity.marriage.Bride;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface BrideMapper {

    Bride convert(BrideCreateRequest source);

    BrideResponse convert(Bride source);
}