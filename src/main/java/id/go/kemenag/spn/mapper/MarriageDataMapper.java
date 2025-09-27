package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.marriage.request.MarriageDataCreateRequest;
import id.go.kemenag.spn.dto.marriage.response.MarriageDataResponse;
import id.go.kemenag.spn.entity.divorce.MarriageData;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface MarriageDataMapper {

    MarriageData convert(MarriageDataCreateRequest source);

    MarriageDataResponse convert(MarriageData source);
}
