package id.go.kemenag.spn.activity.divorce;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.DivorceConstant;
import id.go.kemenag.spn.constant.WorkflowConstant;
import id.go.kemenag.spn.entity.divorce.CaseSchedule;
import id.go.kemenag.spn.entity.divorce.DivorceCase;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.service.divorce.CaseScheduleService;
import id.go.kemenag.spn.service.divorce.DivorceService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class SetDateActivity implements JavaDelegate {

    @Autowired
    private DivorceService divorceService;

    @Autowired
    private CaseScheduleService caseScheduleService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        UUID applicationId = (UUID) delegateExecution.getVariable(WorkflowConstant.APPLICATION_ID_VARIABLE);
        log.info("Starting SetDateActivity for Application ID: {}", applicationId);

        String setDateType = (String) delegateExecution.getVariable(WorkflowConstant.SET_DATE_TYPE_VARIABLE);
        log.info("Date type to be set: {}", setDateType);

        DivorceConstant.SetDateType dateType = DivorceConstant.SetDateType.fromString(setDateType)
            .orElseThrow(() ->
                new BusinessErrorException(HttpStatus.NOT_FOUND, "Date type not found in process variable: " + setDateType)
            );

        DivorceCase divorceCase = this.divorceService.findByApplicationId(applicationId);

        if (divorceCase == null) {
            throw new BusinessErrorException(HttpStatus.NOT_FOUND, "Divorce case not found for Application ID: " + applicationId);
        }
        LocalDate targetStartDate = calculateTargetStartDate(dateType, divorceCase);
        log.info("Calculated target start date for {} is {}", dateType, targetStartDate);

        CaseSchedule newSchedule = this.caseScheduleService.scheduleNextHearing(applicationId, dateType, targetStartDate);
        LocalTime estimatedTime = this.caseScheduleService.estimateHearingTime(newSchedule.getDailyQueueNumber());
        log.info("Successfully scheduled hearing on {} at {} (Queue #{})", newSchedule.getEventDate(), estimatedTime, newSchedule.getDailyQueueNumber());
    }

    private LocalDate calculateTargetStartDate(DivorceConstant.SetDateType dateType, DivorceCase divorceCase) {
        LocalDate today = LocalDate.now();

        return switch (dateType) {
            case RECONCILIATION_DATE -> today.plusDays(30);
            case COUNCIL_DATE -> {
                Optional<CaseSchedule> latestCouncilSchedule = divorceCase.getSchedules()
                    .stream()
                    .filter(schedule -> schedule.getDateType() == DivorceConstant.SetDateType.COUNCIL_DATE)
                    .max(Comparator.comparing(CaseSchedule::getEventDate));

                yield latestCouncilSchedule
                    .map(schedule -> schedule.getEventDate().plusWeeks(1))
                    .orElseGet(() -> today.plusWeeks(1));
            }
            default -> today.plusWeeks(1);
        };
    }
}
