package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.child.request.ChildClaimCreateRequest;
import id.go.kemenag.spn.dto.child.request.ChildCreateRequest;
import id.go.kemenag.spn.dto.child.response.ChildClaimResponse;
import id.go.kemenag.spn.dto.child.response.ChildResponse;
import id.go.kemenag.spn.entity.divorce.Child;
import id.go.kemenag.spn.entity.divorce.ChildClaim;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface ChildMapper {

    Child convert(ChildCreateRequest source);

    ChildResponse convert(Child source);

    ChildClaim convert(ChildClaimCreateRequest source);

    ChildClaimResponse convert(ChildClaim source);
}
