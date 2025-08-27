package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.previouspartner.request.PreviousPartnerCreateRequest;
import id.go.kemenag.spn.entity.marriage.PreviousPartner;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface PreviousPartnerMapper {

    PreviousPartner convert(PreviousPartnerCreateRequest source);
}
