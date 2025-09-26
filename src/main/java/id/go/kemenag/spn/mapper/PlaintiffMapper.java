package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.plaintiff.request.PlaintiffCreateRequest;
import id.go.kemenag.spn.dto.plaintiff.response.PlaintiffResponse;
import id.go.kemenag.spn.entity.divorce.Plaintiff;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface PlaintiffMapper {

    Plaintiff convert(PlaintiffCreateRequest source);

    PlaintiffResponse convert(Plaintiff source);
}
