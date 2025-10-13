package id.go.kemenag.spn.dto.caseschedule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.go.kemenag.spn.constant.DivorceConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseScheduleResponse {

    @JsonProperty("date_type")
    private DivorceConstant.SetDateType dateType;

    @JsonProperty("event_date")
    private LocalDate eventDate;

    @JsonProperty("status")
    private DivorceConstant.ScheduleStatus status;

    @JsonProperty("process_step")
    private Integer processStep;

    @JsonProperty("daily_queue_number")
    private Integer dailyQueueNumber;
}
