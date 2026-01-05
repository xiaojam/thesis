package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.groom.request.GroomFatherUpdateRequest;
import id.go.kemenag.spn.dto.guardian.request.GuardianCreateRequest;
import id.go.kemenag.spn.dto.guardian.request.GuardianUpdateRequest;
import id.go.kemenag.spn.dto.guardian.response.GuardianResponse;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import id.go.kemenag.spn.entity.marriage.Guardian;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface GuardianMapper {

    @Mapping(target = "relationship", source = "relationship", defaultValue = "FATHER")
    Guardian convert(GuardianCreateRequest source);

    GuardianResponse convert(Guardian source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(GuardianUpdateRequest source, @MappingTarget Guardian target);
}
