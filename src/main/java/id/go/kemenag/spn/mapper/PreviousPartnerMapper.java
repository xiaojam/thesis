package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.groom.request.GroomFatherUpdateRequest;
import id.go.kemenag.spn.dto.previouspartner.request.PreviousPartnerCreateRequest;
import id.go.kemenag.spn.dto.previouspartner.request.PreviousPartnerUpdateRequest;
import id.go.kemenag.spn.dto.previouspartner.response.PreviousPartnerResponse;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import id.go.kemenag.spn.entity.marriage.PreviousPartner;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface PreviousPartnerMapper {

    PreviousPartner convert(PreviousPartnerCreateRequest source);

    PreviousPartnerResponse convert(PreviousPartner source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(PreviousPartnerUpdateRequest source, @MappingTarget PreviousPartner target);
}
