package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.groom.request.GroomCreateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomFatherUpdateRequest;
import id.go.kemenag.spn.dto.groom.request.GroomUpdateRequest;
import id.go.kemenag.spn.dto.groom.response.GroomResponse;
import id.go.kemenag.spn.entity.Application;
import id.go.kemenag.spn.entity.marriage.Groom;
import id.go.kemenag.spn.entity.marriage.GroomFather;
import id.go.kemenag.spn.entity.marriage.GroomMother;
import id.go.kemenag.spn.entity.marriage.PreviousPartner;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface GroomMapper {

    Groom convert(GroomCreateRequest source);

    GroomResponse convert(Groom source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copy(GroomUpdateRequest source, @MappingTarget Groom target);
}
