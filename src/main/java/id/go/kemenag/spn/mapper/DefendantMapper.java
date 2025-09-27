package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.defendant.request.DefendantCreateRequest;
import id.go.kemenag.spn.dto.defendant.response.DefendantResponse;
import id.go.kemenag.spn.entity.divorce.Defendant;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface DefendantMapper {

    Defendant convert(DefendantCreateRequest source);

    DefendantResponse convert(Defendant source);
}
