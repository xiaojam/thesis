package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.bride.request.BrideFatherUpdateRequest;
import id.go.kemenag.spn.dto.bride.request.BrideMotherCreateRequest;
import id.go.kemenag.spn.dto.bride.request.BrideMotherUpdateRequest;
import id.go.kemenag.spn.dto.bride.response.BrideMotherResponse;
import id.go.kemenag.spn.entity.marriage.BrideFather;
import id.go.kemenag.spn.entity.marriage.BrideMother;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface BrideMotherMapper {

    BrideMother convert(BrideMotherCreateRequest source);

    BrideMotherResponse convert(BrideMother source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(BrideMotherUpdateRequest source, @MappingTarget BrideMother target);
}
