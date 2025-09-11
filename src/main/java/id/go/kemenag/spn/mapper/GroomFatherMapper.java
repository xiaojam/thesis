package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.bride.request.BrideMotherUpdateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomFatherCreateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomFatherUpdateRequest;
import id.go.kemenag.spn.dto.groom.response.GroomFatherResponse;
import id.go.kemenag.spn.entity.marriage.BrideMother;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface GroomFatherMapper {

    GroomFather convert(GroomFatherCreateRequest source);

    GroomFatherResponse convert(GroomFather source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(GroomFatherUpdateRequest source, @MappingTarget GroomFather target);
}
