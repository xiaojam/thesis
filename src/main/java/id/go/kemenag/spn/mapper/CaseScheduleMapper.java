package id.go.kemenag.spn.mapper;

import id.go.kemenag.spn.dto.caseschedule.response.CaseScheduleResponse;
import id.go.kemenag.spn.entity.divorce.CaseSchedule;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
@MapperConfig
public interface CaseScheduleMapper {

    CaseScheduleResponse convert(CaseSchedule source);
}
