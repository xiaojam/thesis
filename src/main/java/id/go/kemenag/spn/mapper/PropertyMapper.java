package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.property.request.PropertyClaimCreateRequest;
import id.go.kemenag.spn.dto.property.request.SharedPropertyCreateRequest;
import id.go.kemenag.spn.dto.property.response.PropertyClaimResponse;
import id.go.kemenag.spn.dto.property.response.SharedPropertyResponse;
import id.go.kemenag.spn.entity.divorce.PropertyClaim;
import id.go.kemenag.spn.entity.divorce.SharedProperty;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface PropertyMapper {

    PropertyClaim convert(PropertyClaimCreateRequest source);

    PropertyClaimResponse convert(PropertyClaim source);

    SharedProperty convert(SharedPropertyCreateRequest source);

    SharedPropertyResponse convert(SharedProperty source);
}
