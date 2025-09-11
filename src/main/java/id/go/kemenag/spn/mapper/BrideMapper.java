package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.bride.request.BrideCreateRequest;
import id.go.kemenag.spn.dto.bride.request.BrideUpdateRequest;
import id.go.kemenag.spn.dto.bride.response.BrideResponse;
import id.go.kemenag.spn.entity.marriage.Bride;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface BrideMapper {

    Bride convert(BrideCreateRequest source);

    BrideResponse convert(Bride source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(BrideUpdateRequest source, @MappingTarget Bride target);
}