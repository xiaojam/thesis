package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.groom.request.GroomFatherUpdateRequest;
import id.go.kemenag.spn.dto.marriage.request.MarriageCreateRequest;
import id.go.kemenag.spn.dto.marriage.request.MarriageUpdateRequest;
import id.go.kemenag.spn.dto.marriage.response.MarriageResponse;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import id.go.kemenag.spn.entity.marriage.Marriage;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface MarriageMapper {

    Marriage convert(MarriageCreateRequest source);

    MarriageResponse convert(Marriage source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(MarriageUpdateRequest source, @MappingTarget Marriage target);
}
