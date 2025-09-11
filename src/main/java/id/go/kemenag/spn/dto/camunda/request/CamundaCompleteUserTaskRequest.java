package id.go.kemenag.spn.dto.camunda.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CamundaCompleteUserTaskRequest {

    @JsonProperty("process_instance_id")
    private String processInstanceId;

    @JsonProperty("task_names")
    @NotEmpty
    private List<String> taskNames;

    @JsonProperty("result_map")
    @NotEmpty
    private Map<String, Object> resultMap;
}
