package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.marriage.request.MarriageCreateRequest;
import id.go.kemenag.spn.dto.marriage.response.MarriageResponse;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.marriage.Marriage;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface MarriageMapper {

    Marriage convert(MarriageCreateRequest source);

    MarriageResponse convert(Marriage source);
}
