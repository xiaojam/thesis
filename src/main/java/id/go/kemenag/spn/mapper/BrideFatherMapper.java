package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.bride.request.BrideFatherCreateRequest;
import id.go.kemenag.spn.entity.marriage.BrideFather;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface BrideFatherMapper {

    BrideFather convert(BrideFatherCreateRequest source);
}
