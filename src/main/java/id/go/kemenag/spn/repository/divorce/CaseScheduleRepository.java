package id.go.kemenag.spn.repository.divorce;

import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.entity.divorce.CaseSchedule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseScheduleRepository extends CrudRepository<CaseSchedule, UUID> {

    Long countByEventDateAndDivorceCase_CourtCode(LocalDate eventDate, String courtCode);

    Optional<CaseSchedule> findTopByDivorceCase_ApplicationIdAndDateTypeAndStatusOrderByEventDateDesc(
        UUID applicationId,
        DivorceConstant.SetDateType dateType,
        DivorceConstant.ScheduleStatus status
    );
}
