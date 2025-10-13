package id.go.kemenag.spn.service.divorce;

import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.entity.divorce.CaseSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface CaseScheduleService {

    CaseSchedule scheduleNextHearing(UUID caseId, DivorceConstant.SetDateType dateType, LocalDate targetStartDate);

    LocalTime estimateHearingTime(Integer dailyQueueNumber);

    CaseSchedule findTopScheduledCase(
        UUID applicationId,
        DivorceConstant.SetDateType dateType,
        DivorceConstant.ScheduleStatus status
    );

    CaseSchedule save(CaseSchedule caseSchedule);
}
