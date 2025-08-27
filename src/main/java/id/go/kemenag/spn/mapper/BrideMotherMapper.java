package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.bride.request.BrideMotherCreateRequest;
import id.go.kemenag.spn.entity.marriage.BrideMother;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface BrideMotherMapper {

    BrideMother convert(BrideMotherCreateRequest source);
}
