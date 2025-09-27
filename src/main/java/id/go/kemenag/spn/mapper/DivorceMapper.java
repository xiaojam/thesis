package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.divorce.request.DivorceReasonCreateRequest;
import id.go.kemenag.spn.dto.divorce.response.DivorceReasonResponse;
import id.go.kemenag.spn.entity.divorce.DivorceReason;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface DivorceMapper {

    DivorceReason convert(DivorceReasonCreateRequest source);

    DivorceReasonResponse convert(DivorceReason source);
}
