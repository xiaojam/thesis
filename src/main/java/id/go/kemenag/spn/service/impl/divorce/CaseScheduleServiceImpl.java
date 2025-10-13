package id.go.kemenag.spn.service.impl.divorce;

import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.entity.divorce.CaseSchedule;
import id.go.kemenag.spn.repository.divorce.CaseScheduleRepository;
import id.go.kemenag.spn.service.HolidayService;
import id.go.kemenag.spn.service.divorce.CaseScheduleService;
import id.go.kemenag.spn.service.divorce.DivorceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Service
public class CaseScheduleServiceImpl implements CaseScheduleService {

    @Autowired
    private DivorceService divorceService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private CaseScheduleRepository caseScheduleRepository;

    private static final Integer MAX_HEARINGS_PER_DAY = 30;
    private static final Integer DURATION_PER_HEARING_MINUTES = 20;
    private static final LocalTime COURT_START_TIME = LocalTime.of(9, 0);

    @Override
    public CaseSchedule scheduleNextHearing(UUID applicationId, DivorceConstant.SetDateType dateType, LocalDate targetStartDate) {
        var divorceCase = this.divorceService.findByApplicationId(applicationId);
        if (divorceCase == null) {
            throw new IllegalArgumentException("Divorce case not found for application ID: " + applicationId);
        }

        LocalDate availableDate = findNextAvailableCourtDate(targetStartDate, divorceCase.getCourtCode());

        Long existingHearings = countScheduledHearings(availableDate, divorceCase.getCourtCode());
        Integer dailyQueueNumber = existingHearings.intValue() + 1;
        Integer processStep = divorceCase.getSchedules().size() + 1;
        CaseSchedule newSchedule = CaseSchedule.builder()
            .divorceCase(divorceCase)
            .dateType(dateType)
            .eventDate(availableDate)
            .processStep(processStep)
            .dailyQueueNumber(dailyQueueNumber)
            .build();

        divorceCase.getSchedules().add(newSchedule);
        this.divorceService.save(divorceCase);

        return newSchedule;
    }

    @Override
    public LocalTime estimateHearingTime(Integer dailyQueueNumber) {
        if (dailyQueueNumber == null || dailyQueueNumber <= 0) {
            return COURT_START_TIME;
        }
        Long minutesToAdd = (dailyQueueNumber.longValue() - 1) * DURATION_PER_HEARING_MINUTES;
        return COURT_START_TIME.plusMinutes(minutesToAdd);
    }

    @Override
    public CaseSchedule findTopScheduledCase(UUID applicationId, DivorceConstant.SetDateType dateType, DivorceConstant.ScheduleStatus status) {
        return this.caseScheduleRepository.findTopByDivorceCase_ApplicationIdAndDateTypeAndStatusOrderByEventDateDesc(
                applicationId,
                dateType,
                status
            )
            .orElse(null);
    }

    @Override
    public CaseSchedule save(CaseSchedule caseSchedule) {
        return this.caseScheduleRepository.save(caseSchedule);
    }

    private LocalDate findNextAvailableCourtDate(LocalDate startDate, String courtCode) {
        Integer currentYear = startDate.getYear();
        Set<LocalDate> holidays = holidayService.findHolidays(currentYear);
        LocalDate proposedDate = startDate;

        while (true) {
            if (proposedDate.getYear() != currentYear) {
                currentYear = proposedDate.getYear();
                holidays = holidayService.findHolidays(currentYear);
            }

            DayOfWeek day = proposedDate.getDayOfWeek();

            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY || holidays.contains(proposedDate)) {
                proposedDate = proposedDate.plusDays(1);
                continue;
            }

            Long scheduledCount = countScheduledHearings(proposedDate, courtCode);
            if (scheduledCount < MAX_HEARINGS_PER_DAY) {
                return proposedDate;
            } else {
                proposedDate = proposedDate.plusDays(1);
            }
        }
    }

    private Long countScheduledHearings(LocalDate date, String courtCode) {
        return this.caseScheduleRepository.countByEventDateAndDivorceCase_CourtCode(date, courtCode);
    }
}
