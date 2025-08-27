package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.guardian.request.GuardianCreateRequest;
import id.go.kemenag.spn.entity.marriage.Guardian;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface GuardianMapper {

    Guardian convert(GuardianCreateRequest source);
}
